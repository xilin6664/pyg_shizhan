 //控制层 
app.controller('searchController' ,function($scope,$controller   ,searchService, $location){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.searchEntity = {keywords:'华为',category:'', brand:'',spec:{},price:'',sort:'ASC',sortField:'item_price',page:1, size:10};

	$scope.searchResult = {itemList:[]};//保存查询结果
	$scope.initSearch = function () {
	    if($location.search().keywords != '' && $location.search().keywords != undefined){
	        $scope.searchEntity.keywords = $location.search().keywords;
        }
        $scope.search();
    }
    //根据key将数据放入指定位置
	$scope.add2SearchEntity = function (key, value) {
        $scope.searchEntity[key] = value;
        $scope.search();
    }

    $scope.totalPages = 0;
    $scope.search = function () {
        searchService.search($scope.searchEntity).success(function(res){
            $scope.searchResult = res;
            $scope.totalPages = res.totalPages;//获取总页数
            $scope.buildPageNums();//计算分页要显示的五个数数组
        })
    }

    $scope.pageNums = [];//保存要显示的分页数
    $scope.firstDotShow = true;//控制第一个...的显示或隐藏
    $scope.lastDotShow = true;//控制最后...的显示或隐藏
    $scope.buildPageNums = function () {
        var startNum = 0;//记录第一页数
        var endNum = 0;//记录最后一页数
        //1.当总页数<=5，startNum = 1， endNum=总页数, firstDotShow = false; lastDotShow =false
        if($scope.totalPages <= 5){
            startNum = 1;
            endNum = $scope.totalPages;
            $scope.firstDotShow = false;
            $scope.lastDotShow =false;
        } else {
            //2.当总页数> 5时
            if($scope.searchEntity.page <= 3){
                //2.1 当前页<=3,startNum = 1,endNum=5, firstDotShow = false; lastDotShow =true
                startNum = 1;
                endNum = 5;
                $scope.firstDotShow = false;
                $scope.lastDotShow =true;
            } else if($scope.searchEntity.page < $scope.totalPages - 2){
                //2.2 当前页> 3&& 当前页<总页数-2, startNum = 当前页-2；endNum=当前页+2,  firstDotShow = true; lastDotShow =true
                startNum = $scope.searchEntity.page - 2;
                endNum = $scope.searchEntity.page + 2;
                $scope.firstDotShow = true;
                $scope.lastDotShow =true;
            } else {
                //2.3 当前页>=总页数-2，startNum = 总页数-4；endNum=总页数;firstDotShow = true; lastDotShow =false
                startNum = $scope.totalPages - 4;
                endNum = $scope.totalPages;
                $scope.firstDotShow = true;
                $scope.lastDotShow =false;
            }
        }
        //3.根据startNum和endNum循环生成分页数放入数组
        $scope.pageNums = [];
        var index = 0;
        for(var i=startNum; i<=endNum; i++){
            $scope.pageNums[index++] = i;
        }
    }
    //将规格和规格名称加入到searchEntity.spec对象中
    $scope.addSpec2SearchEntity = function (specName, optionName) {
        $scope.searchEntity.spec[specName] = optionName;
        $scope.search();
    }
    //根据key将searchEntity中指定的值设置为''
    $scope.deleteSearchEntity = function (key) {
        $scope.searchEntity[key] = '';
        $scope.search();//查询条件变化，必须重新查询
    }
    //根据key从searchEntity.spec中删除key的key和value
    $scope.deleteSpecSearchEntity = function (key) {
        delete $scope.searchEntity.spec[key];
        $scope.search();//查询条件变化，必须重新查询
    }
    //记录排序方式和排序域名
    $scope.addSortField = function (sortField) {
        $scope.searchEntity.sortField = sortField;
        if('ASC' == $scope.searchEntity.sort){
            //如果是升序，改为降序
            $scope.searchEntity.sort = "DESC";
        } else {
            //如果是降序，改为升序
            $scope.searchEntity.sort = "ASC";
        }
        $scope.search();//查询条件变化，必须重新查询
    }
    //查询当前页
    $scope.searchCurrentPage = function (num) {
        $scope.searchEntity.page = num;
        $scope.search();
    }
    //查询上一页
    $scope.prePageQuery = function () {
        if($scope.searchEntity.page == 1){
            return ;//当前页是第一页，不能查询上一页
        }
        $scope.searchEntity.page = $scope.searchEntity.page-1;
        $scope.search();
    }
    //查询下一页
    $scope.nextPageQuery = function () {
        if($scope.searchEntity.page == $scope.totalPages){
            return ;//当前页是最后一页，不能查询下一页
        }
        $scope.searchEntity.page = $scope.searchEntity.page+1;
        $scope.search();
    }
});	
