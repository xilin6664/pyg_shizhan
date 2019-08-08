 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService, itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
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
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //定义审核状态数组
    $scope.statuses = ['未申请','申请中','审核通过','已驳回'];
    //查询所有的分类数据，以id为下标，将分类名放到数组中
    $scope.itemCatNames = [];//保存所有分类名的数组
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (res) {
            for(var i=0; i<res.length; i++){
                $scope.itemCatNames[res[i].id] = res[i].name
            }
        })
    }
    //更新审核状态方法
	$scope.updateAuditStatus = function (auditStatus) {
		goodsService.updateAuditStatus(auditStatus, $scope.selectIds).success(function (res) {
			alert(res.message);
			if(res.success){
				$scope.reloadList();
				$scope.selectIds = [];
			}
        })
    }
    //根据id将商品的isDelete更新为1-已删除
	$scope.deleteGoods = function () {
        goodsService.deleteGoods($scope.selectIds).success(function (res) {
            alert(res.message);
            if(res.success){
                $scope.reloadList();
                $scope.selectIds = [];
            }
        })
    }
});	
