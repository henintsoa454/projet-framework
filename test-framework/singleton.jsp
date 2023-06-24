<%@ page import="etu1923.framework.fileUpload.FileUpload"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test</title>
</head>
<body>
    <h2>Nombre d'appel de la classe: </h2>
    <p><%out.print(request.getAttribute("nbr"));%></p>
</body>
</html>