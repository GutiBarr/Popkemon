<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>GutiTech - Error interno</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

<header>
    <div class="header-contenido">
        <a href="tienda"><h1>GutiTech</h1></a>
    </div>
</header>

<main>
    <div class="error-contenedor">
        <h2>500 - Error interno del servidor</h2>
        <p>Ha ocurrido un error inesperado. Por favor intentalo de nuevo mas tarde.</p>
        <a href="tienda" class="boton-escape">Volver a la tienda</a>
    </div>
</main>

<footer>
    <p>&copy; GutiTech - Tienda de Informatica</p>
</footer>

</body>
</html>