app.controller('specificationController', function ($scope, $http, specificationService, $controller) {
    $controller('baseController',{$scope:$scope});
    $scope.findAll = function () {
        //发送请求查询所有品牌，展示到列表
        specificationService.findAll().success(function (res) {
            $scope.specificationList = res;
        })
    }

    $scope.findPage=function(page,size){
        specificationService.findPage(page, size).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    $scope.entity = {spec:{}, optionList:[]};;//对应后台接受的实体类
    $scope.save = function () {
        //发送保存请求，接受返回值并重新刷新界面
        specificationService.save($scope.entity).success(function (res) {
            //提示客户
            alert(res.message);
            //如果成功，刷新界面
            if(res.success){
                $scope.reloadList();
            }
        })
    }
    $scope.findOne = function (id) {
        //根据主键查询单个品牌对象，将对象复制给entity模型
        specificationService.findOne(id).success(function (res) {
            $scope.entity = res;
        });
    }

    $scope.delete = function () {
        //1.发送删除请求
        specificationService.delete($scope.selectIds).success(function (res) {
            //2.接口返回值，提示；判断如果成功，刷新界面，清空选中项数组
            alert(res.message);
            if(res.success){
                $scope.reloadList();
                $scope.selectIds = [];
            }
        })
    };

    //插入一行的方法
    $scope.insertRow = function () {
        $scope.entity.optionList.push({});
    }
    //删除一行的方法
    $scope.deleteRow = function (option) {
        var index = $scope.entity.optionList.indexOf(option);
        $scope.entity.optionList.splice(index, 1);
    }
});