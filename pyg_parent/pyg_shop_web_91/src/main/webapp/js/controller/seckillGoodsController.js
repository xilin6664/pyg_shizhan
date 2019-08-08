//控制层
app.controller('seckillGoodsController', function ($scope, $controller, goodsService, itemCatService, typeTemplateService, uploadService, specificationService, seckillGoodsService) {

    $controller('baseController', {$scope: $scope});//继承



    // 读取列表数据绑定到表单中
    $scope.findAll = function() {
        seckillGoodsService.findAll().success(function(response) {
            $scope.list = response;
        });
    }

    // 分页
    $scope.findPage = function(page, rows) {
        seckillGoodsService.findPage(page, rows).success(function(response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;// 更新总记录数
        });
    }

    // 查询实体
    $scope.findOne = function(id) {
        seckillGoodsService.findOne(id).success(function(response) {
            $scope.entity = response;
        });
    }


    //保存
    $scope.entity = {};//对应后台接受的实体类
    $scope.update = function () {
        //发送保存请求，接受返回值并重新刷新界面
        seckillGoodsService.update($scope.entity).success(function (res) {
            //提示客户
            alert(res.message);
            //如果成功，刷新界面
            if(res.success){
                $scope.reloadList();
            }
        })
    }


    //提交审核
    $scope.updateStatus = function (status) {
        seckillGoodsService.updateStatus(status, $scope.selectIds).success(function (res) {
            alert(res.message);
            if(res.success){
                $scope.reloadList();
            }
        })
    }


    //定义审核状态数组
    $scope.statuses = ['已提交','审核通过','未提交'];

});	
