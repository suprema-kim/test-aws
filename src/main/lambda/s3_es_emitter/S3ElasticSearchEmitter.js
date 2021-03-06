/*
 * S3 -> Lambda -> ES for web server logging.
 */

/* Imports */
var AWS = require('aws-sdk');
var LineStream = require('byline').LineStream;
var path = require('path');
var stream = require('stream');

/* Globals */
var esDomain = {
    endpoint: 'vpc-mc-elasticsearch-pspilhbb5z5iwpdow63jizsb6e.ap-northeast-2.es.amazonaws.com',
    region: 'ap-northeast-2',
    index: 'webserver-log',
    doctype: 'application'
};
var endpoint =  new AWS.Endpoint(esDomain.endpoint);
var s3 = new AWS.S3();
var totLogLines = 0;    // Total number of log lines in the file
var numDocsAdded = 0;   // Number of log lines added to ES so far

/*
 * The AWS credentials are picked up from the environment.
 * They belong to the IAM role assigned to the Lambda function.
 * Since the ES requests are signed using these credentials,
 * make sure to apply a policy that permits ES domain operations
 * to the role.
 */
var creds = new AWS.EnvironmentCredentials('AWS');

/*
 * Get the log file from the given S3 bucket and key.  Parse it and add
 * each log record to the ES domain.
 */
function s3LogsToES(bucket, key, context, lineStream, recordStream) {
    var s3Stream = s3.getObject({Bucket: bucket, Key: key}).createReadStream();

    // Flow: S3 file stream -> Log Line stream -> record extractor -> ES
    s3Stream
        .pipe(lineStream)
        .pipe(recordStream)
        .on('data', function(record) {
            postDocumentToES(record, context);
    });

    s3Stream.on('error', function() {
        console.log(
            'Error getting object "' + key + '" from bucket "' + bucket + '".  ' +
            'Make sure they exist and your bucket is in the same region as this function.');
        context.fail();
    });
}

/*
 * Add the given document to the ES domain.
 * If all records are successfully added, indicate success to lambda
 * (using the "context" parameter).
 */
function postDocumentToES(doc, context) {
    var req = new AWS.HttpRequest(endpoint);

    req.method = 'POST';
    req.path = path.join('/', esDomain.index, esDomain.doctype);
    req.region = esDomain.region;
    req.body = doc;
    req.headers['presigned-expires'] = false;
    req.headers['Host'] = endpoint.host;
    req.headers['Content-Type'] = 'application/json';
    console.log('request to ES: ', JSON.stringify(req, null, 2));

    // Sign the request (Sigv4)
    var signer = new AWS.Signers.V4(req, 'es');
    signer.addAuthorization(creds, new Date());

    // Post document to ES
    var send = new AWS.NodeHttpClient();
    send.handleRequest(req, null, function (httpResp) {
        httpResp.on('data', function (chunk) {
            console.log('on data: ' + chunk);
        });

        httpResp.on('end', function (chunk) {
            console.log('on end: ' + chunk);
            numDocsAdded++;
            if (numDocsAdded === totLogLines) {
                // Mark lambda success.  If not done so, it will be retried.
                console.log('All ' + numDocsAdded + ' log records added to ES.');

                // reset counter
                numDocsAdded = 0;
                totLogLines = 0;

                context.succeed();
            }
        });
    }, function (err) {
        console.log('Error: ' + err);
        console.log(numDocsAdded + 'of ' + totLogLines + ' log records added to ES.');
        context.fail();
    });
}

/* Lambda "main": Execution starts here */
exports.handler = function(event, context) {
    console.log('Received event: ', JSON.stringify(event, null, 2));

    /* == Streams ==
    * To avoid loading an entire (typically large) log file into memory,
    * this is implemented as a pipeline of filters, streaming log data from S3 to ES.
    * Flow: S3 file stream -> Log Line stream -> Log extractor -> ES
    */

    // exclude filebeat date and ip address.
    var recordStream = new stream.Transform({objectMode: true})
    recordStream._transform = function(line, encoding, done) {
        var str,
            startIdxOfLog,
            logRecord;

        str = line.toString();
        startIdxOfLog = str.indexOf("{\"@timestamp\"");
        logRecord = str.substr(startIdxOfLog);
        console.log("log record: " + logRecord);
        this.push(logRecord);
        totLogLines ++;
        done();
    };

    event.Records.forEach(function(record) {
        var bucket = record.s3.bucket.name;
        var objKey = decodeURIComponent(record.s3.object.key.replace(/\+/g, ' '));
        s3LogsToES(bucket, objKey, context, new LineStream(), recordStream);
    });
}