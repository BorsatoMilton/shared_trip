<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error 404</title>
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
            rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #FCF8F3;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .contenido404 {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .fondo404 {
            width: 70%;
            max-width: 800px;
        }
    </style>
</head>

<body>

<%@ include file="WEB-INF/header.jsp" %>

<div class="contenido404">
    <img src="resources/images/404.png" alt="404" class="fondo404">
</div>

<%@ include file="WEB-INF/footer.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
