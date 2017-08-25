$(function(){

  var $apps = $('#app_list');

  //GET
  $.ajax({
    type: 'GET',
    url: '/api/app_infos',
    success: function(app_infos){


      $.each(app_infos,function(i,app_info){
        //$apps.append('<li>name: ' + app_info.title + ', apk id: ' + app_info.apk_id + '</li>');
        var $tr = $('<tr><th scope="row">2</th>').append(
            $('<td>').text(app_info.title),
            $('<td>').text(app_info.Duration),
            $('<td>').text(app_info.apk_id)
        ); //.appendTo('#records_table');
        $('#app_table').append($tr);
        console.log($tr.wrap('<p>').html());
      });
    }
  });
});
