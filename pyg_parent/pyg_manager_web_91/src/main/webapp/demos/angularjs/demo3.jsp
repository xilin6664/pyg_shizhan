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
    <title>angluarjs-控制器</title>
    <script src="../../plugins/angularjs/angular.min.js"></script>
    <script>
        function myController($scope) {
            $scope.username = '传智播客';
        }
    </script>
</head>
<body ng-app ng-controller="myController">
    姓名：<input type="text" ng-model="username"/>
    输入的姓名是：{{username}}
</body>
</html>
