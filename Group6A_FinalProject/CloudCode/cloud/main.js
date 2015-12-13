var toUser;
var fromUser;
var message;
var activityType;
var query = new Parse.Query(Parse.Installation);

Parse.Cloud.define("notifyPush",function(request,response){

	getCommonValues(request);

	Parse.Push.send({
    	where: query,
    	data:{
    		alert : fromUser+ message,
    		type : activityType
    	}
    },{
    	success: function(){
    		response.success("Push notification sent");
    	},
    	error: function(){
    		response.error("Push notification not sent");
    	}
    });
});

Parse.Cloud.define("notifyPushForPhoto",function(request,response){
	getCommonValues(request);

	var photoId = request.params.photoId;

	Parse.Push.send({
    	where: query,
    	data:{
    		alert : fromUser+ message,
    		type : activityType,
    		id : photoId
    	}
    },{
    	success: function(){
    		response.success("Push notification sent");
    	},
    	error: function(){
    		response.error("Push notification not sent");
    	}
    });
});


 Parse.Cloud.define("notifyPushForPhotoAccept",function(request,response){
	getCommonValues(request);

	var albumId = request.params.albumObjectId;

	Parse.Push.send({
    	where: query,
    	data:{
    		alert : fromUser+ message,
    		type : activityType,
    		album : albumId
    	}
    },{
    	success: function(){
    		response.success("Push notification sent");
    	},
    	error: function(){
    		response.error("Push notification not sent");
    	}
    });
});

function getCommonValues(request){
	toUser = request.params.toUser;
    fromUser = request.params.fromUser;
    message = request.params.message;
    activityType = request.params.type;

    query.equalTo("user",toUser);
}
