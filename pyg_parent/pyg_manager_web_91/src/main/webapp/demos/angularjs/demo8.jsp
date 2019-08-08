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
    <title>angluarjs-循环对象数组指令</title>
    <script src="../../plugins/angularjs/angular.min.js"></script>
    <script>

        var app = angular.module('myApp',[]);
        //参数1：控制器名，必须和ng-controller的值一致
        //参数2：控制器函数，默认有$scope
        app.controller('myController', function ($scope) {
           $scope.users = [
               {id:'001', name:'李四', age:38},
               {id:'002', name:'王五', age:38}
           ];
        })
    </script>
</head>
<body ng-app="myApp" ng-controller="myController" >
        <table>
            <thead>
                <tr>
                    <th>序号</th>
                    <th>id</th>
                    <th>姓名</th>
                    <th>年龄</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-repeat="user in users">
                    <td>{{$index+1}}</td>
                    <td>{{user.id}}</td>
                    <td>{{user.name}}</td>
                    <td>{{user.age}}</td>
                </tr>
            </tbody>
        </table>
</body>
</html>
