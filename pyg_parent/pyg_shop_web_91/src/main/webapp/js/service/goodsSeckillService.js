//服务层
app.service('goodsSeckillService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../goodsSeckill/findAll');
	}


    this.findItem=function(title){
        return $http.get('../goodsSeckill/findItem'+title);
    }

	//查询实体
	this.findOne=function(id){
		return $http.get('../goodsSeckill/findOne/'+id);
	}

	//增加 
	this.add=function(entity){
		return  $http.post('../goodsSeckill/add',entity );
	}


});

