<%--
  Created by IntelliJ IDEA.
  User: at_10
  Date: 2019/7/11
  Time: 11:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>angularjs-分页</title>
    <link rel="stylesheet" href="../../plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="../../plugins/angularjs/pagination.css">
    <script src="../../plugins/jQuery/jquery-2.2.3.min.js"></script>
    <script src="../../plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src="../../plugins/angularjs/angular.min.js"></script>
    <script src="../../plugins/angularjs/pagination.js"></script>
    <script>
        var testApp = angular.module('test',['pagination']);
        testApp.controller('testController',function ($scope, $http) {
            // 分页控件配置
            // currentPage：当前页；totalItems：总记录数；itemsPerPage：每页记录数；perPageOptions：分页选项；onChange：当页码变更后自动触发的方法
            $scope.paginationConf = {
                currentPage: 1,
                totalItems: 10,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50],
                onChange: function () {
                    $scope.reloadList();//重新加载
                }
            };

            //重新加载列表 数据
            $scope.reloadList = function () {
                //切换页码
                $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
            }

            $scope.findPage=function(page,size){
                $http.get('./pagination_'+page+'.json').success(
                    function(response){
                        $scope.list=response.rows;
                        $scope.paginationConf.totalItems=response.total;//更新总记录数
                    }
                );
            }
        });
    </script>
</head>
<body ng-app="test" ng-controller="testController">
<table border="1">
    <thead>
    <tr>
        <td>主键</td>
        <td>名称</td>
        <td>首字符</td>
    </tr>
    </thead>
    <tbody>
    <tr ng-repeat="entity in list">
        <td>{{entity.id}}</td>
        <td>{{entity.name}}</td>
        <td>{{entity.firstChar}}</td>
    </tr>
    </tbody>
</table>
<tm-pagination conf="paginationConf"/>
</body>
</html>
