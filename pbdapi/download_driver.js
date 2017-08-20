var express = require ('express');
var download_driver = express();
var mongo = require('mongodb');
var Grid = require('gridfs-stream');
var fs = require('fs');

// create or use an existing mongodb-native db instance
var db = new mongo.Db('pbd-demo-db', new mongo.Server("localhost", 3001));
console.log('Running on port 3001...');

var gfs = Grid(db, mongo);

var fs_write_stream = fs.createWriteStream('write-stream.apk');

var readstream = gfs.createReadStream({
    _id:'598e0bf590f32b1b57d8a5a7'
});

readstream.pipe(fs_write_stream);

fs_write_stream.on('close', function () {
     console.log('file has been written fully!');
});
