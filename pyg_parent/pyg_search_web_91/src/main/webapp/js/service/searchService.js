//服务层
app.service('searchService',function($http){
	//根据条件查询商品列表
	this.search=function(searchEntity){
		return $http.post('../search/search', searchEntity);
	}

});
