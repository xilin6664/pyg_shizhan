app.service('specificationService', function ($http) {
    this.findAll = function () {
        return $http.get('../specification/findAll');
    }
    this.save = function (entity) {
        return $http.post('../specification/add', entity);
    }
    this.findPage = function (page, size) {
        return $http.get('../specification/findPage/'+page+"/"+size);
    }
    this.findOne = function (id) {
        return $http.get('../specification/findOne/'+id);
    }
    this.delete = function (selectIds) {
        return $http.get('../specification/delete/'+selectIds);
    }
    //根据模板id查询该模板的规格和规格选项数据
    this.findSpecByTypeTemplateId = function (typeTemplateId) {
        return $http.get('../specification/findSpecByTypeTemplateId/'+typeTemplateId);
    }
})