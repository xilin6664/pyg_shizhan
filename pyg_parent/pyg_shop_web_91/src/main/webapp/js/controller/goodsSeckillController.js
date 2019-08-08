//控制层
app.controller('goodsSeckillController', function ($scope, $controller, goodsService, itemCatService, typeTemplateService, uploadService, specificationService, goodsSeckillService) {

    $controller('baseController', {$scope: $scope});//继承


    $scope.goodsList = [];
    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsSeckillService.findAll().success(function (res) {
            $scope.goodsList = res;
        })
    }

    $scope.ItemsList = [];
    $scope.$watch('entity.goodsName', function (newValue, oldValue) {
        if (undefined != newValue) {
            goodsSeckillService.findOne(newValue).success(function (res) {
                $scope.ItemsList = res;
            })
        }
    })


    //查询实体
    $scope.findOne = function (id) {
        goodsSeckillService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }



    $scope.ItemsList.smallPic='';
    //保存
    $scope.save = function () {
        //获取富文本框内容
        $scope.ItemsList.introduction = editor.html();
        $scope.ItemsList.smallPic=$scope.ItemsList.image;
        serviceObject = goodsSeckillService.add($scope.ItemsList).success(function (res) {
            alert(res.message);
            $scope.ItemsList = [];
            $scope.goodsList = [];
            editor.html('');
            window.location.reload();
        });
    }


});	
