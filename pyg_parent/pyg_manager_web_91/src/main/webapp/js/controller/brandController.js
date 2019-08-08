app.controller('brandController', function ($scope, $http, brandService, $controller) {
    $controller('baseController',{$scope:$scope});
    $scope.findAll = function () {
        //发送请求查询所有品牌，展示到列表
        brandService.findAll().success(function (res) {
            $scope.brandList = res;
        })
    }

    $scope.findPage=function(page,size){
        brandService.findPage(page, size).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    $scope.entity = {};//对应后台接受的实体类
    $scope.save = function () {
        //发送保存请求，接受返回值并重新刷新界面
        brandService.save($scope.entity).success(function (res) {
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
        brandService.findOne(id).success(function (res) {
            $scope.entity = res;
        });
    }

    $scope.delete = function () {
        //1.发送删除请求
        brandService.delete($scope.selectIds).success(function (res) {
            //2.接口返回值，提示；判断如果成功，刷新界面，清空选中项数组
            alert(res.message);
            if(res.success){
                $scope.reloadList();
                $scope.selectIds = [];
            }
        })
    }
});