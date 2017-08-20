var mongoose = require ('mongoose');

//app_info Schema
var app_infoSchema = mongoose.Schema({
  key: String,
  title: String,
  Duration: Number,
  Location:{
    finelocation:{
       Frequency: Number
     },
     coarselocation:{
       Frequency:Number
     }
   },
   Indentifier:{
     DeviceId: String
   }
});

var App_info = module.exports = mongoose.model('app_infos', app_infoSchema);

// Get app_infos
module.exports.getApp_infos = (callback, limit) => {
	App_info.find(callback).limit(limit);
  //console.log(limit);
}

// Add app_info
module.exports.addApp_info = (app_info, callback) => {
	App_info.create(app_info, callback);
}

// Get app_info
module.exports.getApp_infoById = (id, callback) => {
	App_info.findById(id, callback);
}
