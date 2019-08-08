 //控制层 
app.controller('seckillGoodsController' ,function($scope,$controller   ,seckillGoodsService, $location, $interval){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		seckillGoodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.seckillTimeShow = '';//记录秒杀倒计时时间展示
	//查询实体 
	$scope.findOne=function(id){				
		seckillGoodsService.findOne($location.search().id).success(
			function(response){
				$scope.entity= response;
				//1.获取结束时间
                var endTime = new Date($scope.entity.endTime).getTime();//跟1970年相减差值得毫秒数
				//2.获取当前时间
                var currentTime = new Date().getTime();//当前时间跟1970年相减差值得毫秒数
				//3.计算结束时间和当前时间时间差：秒为单位
				var seconds = (endTime - currentTime)/1000;//结束时间和当前时间的时间差秒
				//4.每隔一秒根据总秒数计算：天、小时、分钟、秒
				var seckillTime = $interval(function(){
					//1.判断seconds是否>0，
					if(seconds > 0){
                        //2.是，
						// 2.1根据seconds计算天、小时、分钟、秒，将计算结果拼接成字符串，赋值给seckillTimeShow，
                        $scope.seckillTimeShow = $scope.convertTimeToStr(seconds);
						// 2.2执行seconds--
                        seconds--;
					} else {
                        //3.否，秒杀结束，停止计时：$interval.cancel(seckillTime)
                        $interval.cancel(seckillTime);
					}
				}, 1000);

			}
		);				
	}

	$scope.convertTimeToStr = function (seconds) {
		//1.根据总秒数计算剩余天数
        var days = Math.floor(seconds/(60*60*24))//计算天数，使用Math.floor取整 23.234234234234
		//2.根据总秒数计算剩余小时数：(总秒数-剩余天的秒数)/(60*60)
		var hours = Math.floor((seconds - days*24*60*60)/(60*60));//19.234234234
		//3.根据总秒数计算剩余分钟数：（总秒数-剩余天的秒数-剩余小时的秒数）/60
        var minutes = Math.floor((seconds-days*24*60*60-hours*60*60)/60);//23.23423423424
		//4.根据总秒数计算剩余秒数：总秒数-剩余天的秒数-剩余小时的秒数-剩余分钟的秒数
		var second = Math.floor(seconds-days*24*60*60-hours*60*60-minutes*60);//23.23423423432
		//5.拼接成字符串返回
		if(second < 10){
			second = "0"+second;
		}
		return days+"天"+hours+"小时"+minutes+"分钟"+second+"秒";
    }
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckillGoodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=seckillGoodsService.add( $scope.entity  );//增加 
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
		seckillGoodsService.dele( $scope.selectIds ).success(
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
		seckillGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//从redis中查询秒杀商品列表展示
	$scope.goodsList = [];//保存秒杀商品列表
	$scope.findSeckillGoodsList = function () {
        seckillGoodsService.findAll().success(function (res) {
            $scope.goodsList = res;
        })
    }

    $scope.jumpToGoodsDetail = function (id) {
		location.href="seckill-item.html#?id="+id;
    }

    $scope.saveOrder = function () {
		seckillGoodsService.saveOrder($scope.entity.id).success(function (res) {
			//1.成功，跳转到支付页
			if(res.success){
				location.href = "pay.html";
			} else {
                //2.失败，提示失败
				alert(res.message);
			}
        })
    }
});	
