<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Popkemon - ${producto.nombre}</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

<header>
    <div class="header-contenido">
        <a href="tienda"><h1>Popkemon</h1></a>
        <nav>
            <a href="tienda">Tienda</a>
            <a href="carrito">Carrito</a>
            <c:choose>
                <c:when test="${not empty sessionScope.usuario}">
                    <a href="perfil">${sessionScope.usuario.nombre}</a>
                    <a href="pedidos">Mis Pedidos</a>
                    <a href="logout">Cerrar sesion</a>
                </c:when>
                <c:otherwise>
                    <a href="login">Iniciar sesion</a>
                    <a href="registro">Registrarse</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<main>

    <div class="detalle-producto">
        <img src="imagenes/${producto.imagen}" alt="${producto.nombre}" />

        <div class="detalle-info">
            <h2>${producto.nombre}</h2>
            <p class="categoria">${producto.categoria.nombre}</p>
            <p class="marca"><strong>Marca:</strong> ${producto.marca}</p>
            <p class="descripcion">${producto.descripcion}</p>
            <p class="precio">
                <fmt:formatNumber value="${producto.precio}" pattern="#,##0.00" /> &euro;
            </p>

            <form method="post" action="carrito">
                <input type="hidden" name="accion" value="anadir" />
                <input type="hidden" name="idProducto" value="${producto.idProducto}" />
                <label for="cantidad">Cantidad:</label>
                <input type="number" id="cantidad" name="cantidad" value="1" min="1" max="10" />
                <button type="submit">Añadir al carrito</button>
            </form>

            <a href="tienda" class="boton-escape">Volver a la tienda</a>
        </div>
    </div>

</main>

<footer>
    <p>&copy; Popkemon - Tienda de Funko Pops Pokemon</p>
</footer>

</body>
</html>