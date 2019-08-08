//服务层
app.service('cartService',function($http){
	    	
	//将商品加入购物车方法
	this.addItemToCartList=function(itemId, num){
		return $http.get('../cart/addItemToCartList/'+itemId+"/"+num);
	}
	//查询购物车列表
    this.findCartList = function () {
        return $http.get('../cart/findCartList');
    }
});
