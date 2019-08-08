 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService, 
										   itemCatService, typeTemplateService, uploadService, specificationService){
	
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
		//1.判断是商品管理还是上下架
		if($scope.searchEntity.auditStatus == '2'
				&& $scope.searchEntity.isDelete == '0'){
            //3.如果是上下架，调用search
            goodsService.search(page,rows, $scope.searchEntity).success(
                function(response){
                    $scope.list=response.rows;
                    $scope.paginationConf.totalItems=response.total;//更新总记录数
                }
            );
		} else {
            //2.如果是商品管理，调用findPage
            goodsService.findPage(page,rows).success(
                function(response){
                    $scope.list=response.rows;
                    $scope.paginationConf.totalItems=response.total;//更新总记录数
                }
            );
		}

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
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.tbGoodsDesc.introduction = editor.html();
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//清空界面准备下次录入
                    $scope.entity = {tbGoods:{}, tbGoodsDesc:{}, itemList:[]};
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

	$scope.itemCat1List = [];//保存一级分类数组
	//根据parentId=0查询一级分类
	$scope.findCategory1List = function (parentId) {
		itemCatService.findByParentId(parentId).success(function (res) {
            $scope.itemCat1List = res;
        })
    }
    $scope.entity = {tbGoods:{}, tbGoodsDesc:{itemImages:[],specificationItems:[]}, itemList:[{spec:{},price:100,num:9999,status:0,isDefault:0}]};
    $scope.itemCat2List = [];//保存二级分类
    //watch监控函数，可以监控变量或函数的返回值变化，在变化时触发指定的函数
	//参数1：要监控的变量或者是要监控的函数
	//参数2：在变化时要调用的函数，该函数有两个参数：参数1：变化之后的值；参数2：变化之前的值
	//监控一级分类选项变化
	$scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
		if(undefined != newValue){
            // 根据newValue查询二级分类，展示到二级分类的下拉框
            itemCatService.findByParentId(newValue).success(function (res) {
                $scope.itemCat2List = res;
                $scope.itemCat3List = [];
                $scope.entity.tbGoods.typeTemplateId = '';
            })
		}
    })
    $scope.itemCat3List = [];//保存三级分类
    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        if(undefined != newValue){
            // 根据newValue查询二级分类，展示到二级分类的下拉框
            itemCatService.findByParentId(newValue).success(function (res) {
                $scope.itemCat3List = res;
                $scope.entity.tbGoods.typeTemplateId = '';
            })
		}
    })
	//监控三级分类变化，获取选中的三级分类的typeId
    $scope.$watch('entity.tbGoods.category3Id', function (newValue, oldValue) {
        if(undefined != newValue){
            itemCatService.findOne(newValue).success(function (res) {
                $scope.entity.tbGoods.typeTemplateId = res.typeId;
            })
		}

    })

	$scope.brandList = [];//保存品牌数组
	$scope.specList = [];//保存根据模板查询到规格数据
	//监控模板id的变化，根据模板获取该模板的品牌数组，回显到品牌下拉框
    $scope.$watch('entity.tbGoods.typeTemplateId', function (newValue, oldValue) {
        if(undefined != newValue){
            typeTemplateService.findOne(newValue).success(function (res) {
                $scope.brandList = JSON.parse(res.brandIds);
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(res.customAttributeItems)
				res.specIds
            })
			//调用规格service根据模板id查询该模板对应的规格和规格选项返回
			specificationService.findSpecByTypeTemplateId(newValue).success(function (res) {
                $scope.specList = res;
            })
        }
    })

	$scope.image = {color:'',url:''};
	$scope.uploadFile = function () {
		//调用uploadService实现上传
		uploadService.uploadFile().success(function (res) {
			if(res.success){
                //成功，回显数据
				$scope.image.url = res.message;
			} else {
                //失败，提示
				alert(res.message);
			}
        })
    }

    //插入一行图片对象
	$scope.insertRow = function () {
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image);
    }
    //删除一行图片
	$scope.deleteRow = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
    }
    //动态保存选中的规格选项
	$scope.selectSpecs = function ($event, specName, optionName) {
		//1.根据规格名从数组中查询要保存对象
		var specObject = $scope.searchObjectByKeyFromArray($scope.entity.tbGoodsDesc.specificationItems, specName, "attributeName")

		if(null != specObject){
            //2.查询到对象，判断是否选中
            if($event.target.checked){
                //2.1选中，将规格选项名加入到attributeValue对应的数组
                specObject.attributeValue.push(optionName);
			} else {
                //2.2未选中，将数据从attributeValue对应的数组删除
                var index = specObject.attributeValue.indexOf(optionName);
                specObject.attributeValue.splice(index, 1);
                //2.3判断specObject.attributeValue是否为空数组，如果是空数组，把该对象从$scope.entity.tbGoodsDesc.specificationItems删除
				if(specObject.attributeValue.length <= 0){
                    index = $scope.entity.tbGoodsDesc.specificationItems.indexOf(specObject);
                    $scope.entity.tbGoodsDesc.specificationItems.splice(index, 1);
				}
			}
		} else {
            //3.未查询到对象，创建对象，将规格和规格选项保存，将对象放到数组
            specObject = {attributeName:specName, attributeValue: [optionName]};
            $scope.entity.tbGoodsDesc.specificationItems.push(specObject);
		}

		//根据选中的规格和规格选项生成库存列表
        $scope.createItemList = function () {
			//1.将entity.itemList数组初始化成：[{spec:{},price:100,num:9999,status:0,isDefault:0}]
			$scope.entity.itemList = [{spec:{},price:100,num:9999,status:0,isDefault:0}];
			//2.遍历$scope.entity.tbGoodsDesc.specificationItems数组，获取一个对象
			for(var i=0; i<$scope.entity.tbGoodsDesc.specificationItems.length; i++){
                //3.将获取的对象和entity.itemList数组中所有的对象合并，
                //4.将合并结果赋值给entity.itemList,下次循环继续使用
                $scope.entity.itemList = $scope.addColumn($scope.entity.tbGoodsDesc.specificationItems[i], $scope.entity.itemList);
            }
        }

        $scope.addColumn = function (specObject, itemList) {
			//specObject：{attributeName:'网络', attributeValue:[3G,4G]}
			//1.循环itemList，获取一个对象：{spec:{},price:100,num:9999,status:0,isDefault:0}
			var newItemList = [];
			for(var i=0; i<itemList.length; i++){
                var item = itemList[i];
                //2.循环specObject.attributeValue数组，进行合并{spec:{'网络'："3G"},price:100,num:9999,status:0,isDefault:0}
				for(var j=0; j<specObject.attributeValue.length; j++){
					//创建新对象：先将item转换成字符串，将字符串转换成对象
					var newItem = JSON.parse(JSON.stringify(item));
                    newItem.spec[specObject.attributeName] = specObject.attributeValue[j];
                    newItemList.push(newItem);
				}
            }
            return newItemList;
        }
    }

    $scope.initSpecItemsAndItemList = function () {
		$scope.entity.tbGoodsDesc.specificationItems = [];
        $scope.entity.itemList = [{spec:{},price:100,num:9999,status:0,isDefault:0}];
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

    $scope.updateIsMarketable = function (isMarketable) {
		goodsService.updateIsMarketable(isMarketable, $scope.selectIds).success(function (res) {
			alert(res.message);
			if(res.success){
				$scope.reloadList();
			}
        })
    }
});	
