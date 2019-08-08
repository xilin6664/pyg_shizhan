<%--
  Created by IntelliJ IDEA.
  User: at_10
  Date: 2019/7/11
  Time: 9:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>angluarjs-初始化指令</title>
    <script src="../../plugins/angularjs/angular.min.js"></script>
    <script>

        var app = angular.module('myApp',[]);
        //参数1：控制器名，必须和ng-controller的值一致
        //参数2：控制器函数，默认有$scope
        app.controller('myController', function ($scope) {
            $scope.initUserName = function () {
                $scope.username = '九纹龙';
            }
        })
    </script>
</head>
<body ng-app="myApp" ng-controller="myController" ng-init="initUserName()">
    姓名：<input type="text" ng-model="username"/>
    输入的姓名是：{{username}}
</body>
</html>
