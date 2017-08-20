var assert = require('assert');
var fs = require('fs');
var mongodb = require('mongodb');

var uri = 'mongodb://localhost/pbd-db-demo';

mongodb.MongoClient.connect(uri, function(error, db) {
  assert.ifError(error);

  var bucket = new mongodb.GridFSBucket(db);

  fs.createReadStream('./apk_fils/app-debug.apk').
    pipe(bucket.openUploadStream('app-debug.apk')).
    on('error', function(error) {
      assert.ifError(error);
    }).
    on('finish', function() {
      console.log('done!');
      process.exit(0);
    });
});
