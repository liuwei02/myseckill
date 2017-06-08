<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!-- 引入jstl标签库 -->
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>商品秒杀列表</title>
    <%@include file="common/head.jsp"%>
</head>
<body>
    <div class="container">
        <div class="panel panel-default">
            <div class="panel-heading text-center">
                <h2>秒杀</h2>
                <div class="panel-body">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>库存</th>
                                <th>开始时间</th>
                                <th>结束时间</th>
                                <th>创建时间</th>
                                <th>详情页</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:foreach var="sk" items="${data}" >
                                <tr>
                                    <td>${sk.name}</td>
                                    <td>${sk.number}</td>
                                    <td>
                                        <fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </td>
                                    <td><<a class="btn btn-info" href="/seckill/{sk.seckillId}/detail">link</a>></td>
                                </tr>
                            </c:foreach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
<!-- jQuery (Bootstrap 的 JavaScript 插件需要引入 jQuery) -->
<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
<!-- 包括所有已编译的插件 -->
<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</html>