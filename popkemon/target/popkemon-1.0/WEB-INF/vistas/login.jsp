<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Popkemon - Iniciar sesion</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

<header>
    <div class="header-contenido">
        <a href="tienda"><h1>Popkemon</h1></a>
        <nav>
            <a href="tienda">Tienda</a>
            <a href="registro">Registrarse</a>
        </nav>
    </div>
</header>

<main>

    <div class="formulario-contenedor">
        <h2>Iniciar sesion</h2>

        <c:if test="${not empty sessionScope.mensaje}">
            <div class="mensaje error">
                <c:out value="${sessionScope.mensaje}" />
                <c:remove var="mensaje" scope="session" />
            </div>
        </c:if>

        <form method="post" action="login">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required />

            <label for="password">Contrasena</label>
            <input type="password" id="password" name="password" required />

            <button type="submit">Iniciar sesion</button>
        </form>

        <p>¿No tienes cuenta? <a href="registro">Registrate aqui</a></p>
        <a href="tienda" class="boton-escape">Volver a la tienda</a>
    </div>

</main>

<footer>
    <p>&copy; Popkemon - Tienda de Funko Pops Pokemon</p>
</footer>

</body>
</html>