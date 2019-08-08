 //控制层 
app.controller('cartController' ,function($scope,$controller   ,cartService,$location,addressService, orderService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.resultMap = {};
    $scope.addItemToCartList = function () {
		//1.获取请求地址中的itemId和num
        var itemId = $location.search().itemId;
        var num = $location.search().num;
        //2.调用service将参数发送到后台
		cartService.addItemToCartList(itemId, num).success(function (res) {
            //3.接受相应结果，如果成功，提示成功，显示商品
            //4.失败，提示错误，不显示商品
            $scope.resultMap = res;
            //如果成功，根据itemId查询数据进行回显
        })
    }
    //查询购物车列表的方法
    $scope.cartList = [];//保存购物车列表
    $scope.totalFees = 0;//所有订单的总金额
    $scope.totalNums = 0;//所有订单商品个数总和
    $scope.findCartList = function () {
        cartService.findCartList().success(function (res) {
            $scope.totalFees = 0;
            $scope.totalNums = 0;
            $scope.cartList = res;
            for(var i=0;i< $scope.cartList.length; i++){
                var cart = $scope.cartList[i];
                var orderItemList = cart.orderItemList;
                for(var j=0; j<cart.orderItemList.length; j++){
                    var orderItem = orderItemList[j];
                    $scope.totalNums = $scope.totalNums + orderItem.num;
                    $scope.totalFees = $scope.totalFees+orderItem.totalFee;
                }
            }
        })
    }

    $scope.dynamicAddItemToCartList = function (itemId, num) {
        //2.调用service将参数发送到后台
        cartService.addItemToCartList(itemId, num).success(function (res) {
            if(res.success){
                //刷新，从新查询购物车里列表
                $scope.findCartList();
            } else {
                //错误，提示错误信息
                alert(res.message);
            }
        })
    }

    //根据登录用户查询该用户的收货地址列表
    $scope.addressList = [];//存储收货地址列表
    $scope.address = {};//代表要选中的收货人地址
    $scope.findAddressByUser = function () {
        addressService.findAddressByUser().success(function (res) {
            $scope.addressList = res;
            //循环收货人列表，将isDefault=1收货人对象赋值给$scope.address
            for(var i=0; i<$scope.addressList.length; i++){
                var addressT = $scope.addressList[i]
                if(addressT.isDefault == '1'){
                    $scope.address = addressT;
                }
            }
        })
    }

    $scope.entity = {paymentType:'1'};//保存订单数据
    $scope.selectAddress = function (addressT) {
        $scope.address = addressT;
    }

    $scope.selectPaymentType = function (paymentType) {
        $scope.entity.paymentType = paymentType;
    }

    $scope.saveOrder = function () {
        //1.将收货人信息保存到订单对象
        $scope.entity.receiverAreaName=$scope.address.address;//地址
        $scope.entity.receiverMobile=$scope.address.mobile;//手机
        $scope.entity.receiver=$scope.address.contact;//联系人
        //2.发送订单保存请求，接受响应
        orderService.add($scope.entity).success(function (res) {
            if(res.success){
                //3.成功，根据支付类型跳转：
                if($scope.entity.paymentType == '1'){
                    //1-微信支付，跳转到微信扫码支付页；
                    location.href='pay.html';
                } else {
                    //0-货到付款-直接跳转成功提示界面
                    location.href="paysuccess.html";
                }
            } else {
                //4.失败，提示失败，跳转失败界面
                location.href="payfail.html";
            }
        })
    }
});	
