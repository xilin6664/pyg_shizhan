//服务层
app.service('uploadService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.uploadFile=function(){
		//1.将文件数据放到表单对象中
        //FormData 就是 XMLHttpRequest Level 2 新增的一个对象，利用它来提交表单、模拟表单提交，当然最大的优势就是可以上传二进制文件
		var formData = new FormData();
		formData.append('file', file.files[0])
		//2.提交表单数据：post、enctype=multipart/form-data
		return $http({
			method:'post',
			url: '../upload/uploadFile',
			data: formData,
            headers:{'Content-type':undefined},
            transformRequest: angular.identity
		});
	}

});
