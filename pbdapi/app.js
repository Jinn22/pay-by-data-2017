var express = require ('express');
var app = express();
var bodyParser = require ('body-parser');
var mongoose = require ('mongoose');

app.use(bodyParser.json());

App_info = require('./models/app_info');

//connect to mongoose
mongoose.connect('mongodb://localhost/pbd-db-demo');

// modules for gridfs
var Grid = require('gridfs-stream');
var conn = mongoose.connection;
var Schema = mongoose.Schema;
Grid.mongo = mongoose.mongo;

app.get('/',function(req, res){
  res.send('Hello World!');
});

app.get('/api/app_infos', (req, res) => {
	App_info.getApp_infos((err, app_info) => {
		if(err){
			throw err;
		}
    console.log(app_info.toString());
		res.json(app_info);
	});
});

app.post('/api/app_info', (req, res) => {
	var app_info = req.body;
	App_info.addApp_info(app_info, (err, app_info) => {
		if(err){
			throw err;
		}
		res.json(app_info);
	});
});

app.get('/api/app_info/:_id', (req, res) => {
  App_info.getApp_infoById(req.params._id, (err, app_info) => {
		if(err){
			throw err;
		}
		res.json(app_info);
	});
});

app.get('/api/get_apk/:_id', (req, res) => {
  mongoose.connect('mongodb://localhost/pbd-db-demo',{ useMongoClient: true });
  var conn = mongoose.connection;
  var gfs = new Grid(conn);
  //make sure db instance is open before passing into 'Grid'
  conn.once('open',function(err){
    //if(err) return handleError(err);
    console.log('open');

    console.log("success");
  });

  try{
    var readStream = gfs.createReadStream({
      _id: req.params._id
      //filename:"app-debug.apk"
    });
    //res.set('Content-Type', 'application/binary');
    readStream.pipe(res);
  } catch(err){
    console.log(err);
  }
});

app.listen(3000);
console.log('Running on port 3000...');
