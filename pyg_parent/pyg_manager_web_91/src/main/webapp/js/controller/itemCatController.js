 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId = $scope.parentId;//给parentId赋值
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询，根据直接上级的id作为parentId查询最新下级数据
					itemCatService.findByParentId($scope.parentId).success(function (res) {
						$scope.categoryList = res;
                    })
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	$scope.categoryList = [];//保存商品分类查询结果列表
    //页面初始化时查询一级分类方法
	$scope.findCategory1List = function () {
		//parentId == 0
        itemCatService.findByParentId(0).success(function (res) {
            $scope.categoryList = res;
        })
    }

    $scope.grade = 1;//记录列表展示的当前级别
	$scope.itemCat1 = null;//记录点击的一级分类对象
	$scope.itemCat2 = null;//记录点击的二级分类对象
	$scope.parentGradeShow = '顶级分类';//保存当前新增级别的上级分类名
	$scope.parentId = 0;//记录当前新增级别直接上级id，默认是0
    //根据当前点击的级别，查询它的下一级
	$scope.searchNext = function (curItemCat) {
		if($scope.grade == 1){
            //1.grade == 1，$scope.itemCat1 = null；$scope.itemCat2 = null
            //1.grade == 1，$scope.parentGradeShow = '顶级分类';
            //1.grade == 1，$scope.parentId = 0;
            $scope.itemCat1 = null;//记录点击的一级分类对象
            $scope.itemCat2 = null;//记录点击的二级分类对象
            $scope.parentGradeShow = '顶级分类';
            $scope.parentId = 0;
        } else if($scope.grade == 2){
            //2.grade == 2，$scope.itemCat1 = curItemCat； $scope.itemCat2 = null
            //2.grade == 2，$scope.parentGradeShow = curItemCat.name;
            //2.grade == 2，$scope.parentId = curItemCat.id;
            $scope.itemCat1 = curItemCat;
            $scope.itemCat2 = null;
            $scope.parentGradeShow = curItemCat.name;
            $scope.parentId = curItemCat.id;
		} else {
            //3.grade == 3，$scope.itemCat2 = curItemCat
            //3.grade == 3，$scope.parentGradeShow = $scope.itemCat1.name+' >> '+curItemCat.name; 一级分类名 >> 二级分类名
            //3.grade == 3，$scope.parentId = curItemCat.id;
            $scope.itemCat2 = curItemCat;
            $scope.parentGradeShow = $scope.itemCat1.name+' >> '+curItemCat.name;
            $scope.parentId = curItemCat.id;
        }

		//根据当前点击级别的id为parentId，查询下一级
        itemCatService.findByParentId(curItemCat.id).success(function (res) {
            $scope.categoryList = res;
        })
    }

    $scope.setGrade = function (grade) {
		$scope.grade = grade;
    }
});	
