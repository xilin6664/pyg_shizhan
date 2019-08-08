//服务层
app.service('indexService',function($http){
	    	
	this.findAll=function(){
		return $http.get('../itemCat/findAll');		
	}

	this.findLoginUser = function () {
		return $http.get('../index/findLoginUser');
    }
});
