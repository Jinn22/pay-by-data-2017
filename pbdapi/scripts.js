$(function(){

  var $apps = $('#app_list');

  //GET
  $.ajax({
    type: 'GET',
    url: '/api/app_infos',
    success: function(app_infos){


      $.each(app_infos,function(i,app_info){

          var tr = '<tr><th scope="row">'+ i +'</th><td>'+app_info.title+'</td><td>'+app_info.Duration+'</td><td>'+app_info.apk_id+'</td></tr>';
        $('#app_table').append(tr);
        console.log(tr);
      });
    }
  });
});
