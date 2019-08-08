<%--
  Created by IntelliJ IDEA.
  User: at_10
  Date: 2019/8/3
  Time: 11:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>二维码入门小demo</title>
</head>
<body>
<img id="qrious">
<script src="../../plugins/qrious/qrious.js"></script>
<script>
    var qr = new QRious({
        element:document.getElementById('qrious'),
        size:250,
        level:'H',
        value:'http://www.itcast.cn'
    });
</script>
</body>
</html>