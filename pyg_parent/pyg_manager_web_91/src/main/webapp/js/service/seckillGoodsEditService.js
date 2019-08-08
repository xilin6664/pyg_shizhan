//服务层
app.service('seckillGoodsEditService',function($http){

    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../seckillGoods/findAll');
    }
    //分页 
    this.findPage=function(page,rows){
        return $http.get('../seckillGoods/findPage/'+page+'/'+rows);
    }
    //查询实体
    this.findOne=function(id){
        return $http.get('../seckillGoods/findOne/'+id);
    }

    //提交审核
    this.updateStatus=function(status,selectIds){
        return $http.get('../seckillGoods/updateStatus/'+status+"/"+selectIds);
    }

    //修改 
    this.update=function(entity){
        return  $http.post('../seckillGoods/update/',entity );
    }


});

