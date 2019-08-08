//服务层
app.service('payService',function($http){
	    	
	//发送生成二维码的请求
	this.createNative=function(){
		return $http.get('../pay/createNative');
	}

	this.queryPayResultStatus = function () {
        return $http.get('../pay/queryPayResultStatus');
    }
});
