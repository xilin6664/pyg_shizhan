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
    <title>angluarjs-循环数组指令</title>
    <script src="../../plugins/angularjs/angular.min.js"></script>
    <script>

        var app = angular.module('myApp',[]);
        //参数1：控制器名，必须和ng-controller的值一致
        //参数2：控制器函数，默认有$scope
        app.controller('myController', function ($scope) {
           $scope.user = [1,'张三',25];
        })
    </script>
</head>
<body ng-app="myApp" ng-controller="myController" >
        <ul>
            <li ng-repeat="info in user" >
                {{info}}
            </li>
        </ul>
</body>
</html>
