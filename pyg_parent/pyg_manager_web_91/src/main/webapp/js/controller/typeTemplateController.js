 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //查询所有的品牌列表数据，返回并放到指定的变量上
	$scope.brandList = {data:[]};//多选下拉框默认数据结构
	$scope.findBrandList = function () {
        brandService.findAll().success(function (res) {
            $scope.brandList.data = res;
        })
    }

    //查询所有的规格列表数据，返回并放到指定的变量上
    $scope.specList = {data:[]};//多选下拉框默认数据结构
    $scope.findSpecList = function () {
        specificationService.findAll().success(function (res) {
            $scope.specList.data = res;
        })
    }
    $scope.entity = {customAttributeItems:[]};

    $scope.insertRow = function () {
        $scope.entity.customAttributeItems.push({});
    }
    $scope.deleteRow = function (custAttr) {
        var index = $scope.entity.customAttributeItems.indexOf(custAttr);
        $scope.entity.customAttributeItems.splice(index, 1);
    }
});	
