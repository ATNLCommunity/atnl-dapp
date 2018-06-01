api_url = "http://atnl.9000meng.com/app/";

var post = function (api, value, callback) {
	$.ajax({
		url : api_url + api,
		type : 'POST',
		data : value,
		async : true,
		success : function (data) {
			callback(data);
		}
	});
}

function upload (url, fileId, callback)
{
	$.ajaxFileUpload({
		url : api_url + api,
		type: 'POST',
		secureuri : false,
		fileElementId : fileId,
		dataType: 'json',
		success: function (data, status)
		{

			callback(data.data.file);
		},
		error: function (data, status, e)
		{
			ErrorCode('ERROR_AJAXFILE_FAILED');
		}
	});
}

function getParam(key)
{
   var query = window.location.search.substring(1);
   var vars = query.split("&");
   for (var i=0;i<vars.length;i++) {
		   var pair = vars[i].split("=");
		   if(pair[0] == key){return pair[1];}
   }
   return ("");
}


