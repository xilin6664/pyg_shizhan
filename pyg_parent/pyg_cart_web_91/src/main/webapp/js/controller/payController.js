 //控制层 
app.controller('payController' ,function($scope,$controller   ,payService){
	
	$controller('baseController',{$scope:$scope});//继承

    /**
	 * 创建支付二维码的方法
     */
	$scope.createNative = function () {
        payService.createNative().success(function (res) {
			//成功，获取二维码地址，使用qrious生成二维码并展示
			if(res.success){
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:res.message
                });
                //二维码生成完成，触发查询用户支付结果
                $scope.queryPayResultStatus();
			} else {
                //失败，提示失败
				alert(res.message);
            }
        })
    }

    $scope.queryPayResultStatus = function () {
        payService.queryPayResultStatus().success(function (res) {
            if(res.success){
                //跳转支付成功界面，paysuccess.html
                location.href="paysuccess.html";
            } else {
                //判断是否超时，如果是超时，重新生成二维码
                if(res.message == '查询超时'){
                    $scope.createNative();
                } else {
                    //不是超时，说明支付失败，提示客户支付失败
                    location.href = "payfail.html";
                }
            }
        })
    }
});	
