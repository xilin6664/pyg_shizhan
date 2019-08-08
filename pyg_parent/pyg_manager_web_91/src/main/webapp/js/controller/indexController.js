 //控制层 
app.controller('indexController' ,function($scope,$controller,indexService){
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.username = '';//当前登录的用户名

	$scope.findLoginUser = function () {
        indexService.findLoginUser().success(function (res) {
			$scope.username = res.username;
        })
    }
});	
