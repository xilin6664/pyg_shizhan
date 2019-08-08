app.controller('baseController', function ($scope, $http) {

    // 分页控件配置
    // currentPage：当前页；totalItems：总记录数；itemsPerPage：每页记录数；perPageOptions：分页选项；onChange：当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    //重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    $scope.selectIds = [];//保存选中id的数组
    $scope.selectOptions = function ($event, id) {
        //1.判断选项是否选中
        if($event.target.checked){
            //2.选中，将选中的id保存到selectIds数组中
            $scope.selectIds.push(id);
        } else {
            //3.未选中，将id从数组selectIds中删除
            var index = $scope.selectIds.indexOf(id);//获取元素在数组中的下标
            $scope.selectIds.splice(index, 1);//参数1：从什么位置开始删，参数2，删除几个
        }
    }

    //将json字符串转换成json数组，遍历获取值拼接的方法
    $scope.jsonToString = function (jsonStr, key) {
        //1.parse将json字符串转换成json数组
        var jsonArr = JSON.parse(jsonStr);//将符合要求的json字符串转换成json对象或数组；
        //2.遍历数组，根据key获取值拼接
        var tmpArr = new Array();
        for(var i=0; i<jsonArr.length; i++){
            tmpArr.push(jsonArr[i][key]);
        }
        //3.将拼接结果返回
        return tmpArr.join(',');
    }
    //定义html内容过滤器

});

app.filter('trustHtml', function ($sce) {
    return function (text) {
        return $sce.trustAsHtml(text);
    }
})