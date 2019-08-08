<%--
  Created by IntelliJ IDEA.
  User: at_10
  Date: 2019/7/17
  Time: 11:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>kindeditor-富文本编辑器</title>
    <link rel="stylesheet" href="../../plugins/kindeditor/themes/default/default.css" />
    <script charset="utf-8" src="../../plugins/kindeditor/kindeditor-min.js"></script>
    <script charset="utf-8" src="../../plugins/kindeditor/lang/zh_CN.js"></script>
</head>
<body>
    <textarea id="kindArea"></textarea>
    <script>
        KindEditor.ready(function (K) {
            window.editor = K.create('#kindArea');
        })
    </script>
</body>
</html>
