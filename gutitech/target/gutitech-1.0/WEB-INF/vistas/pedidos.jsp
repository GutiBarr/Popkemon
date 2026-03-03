<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>GutiTech - Mis Pedidos</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

<header>
    <div class="header-contenido">
        <a href="tienda"><h1>GutiTech</h1></a>
        <nav>
            <a href="tienda">Tienda</a>
            <a href="carrito">Carrito</a>
            <a href="perfil">${sessionScope.usuario.nombre}</a>
            <a href="pedidos">Mis Pedidos</a>
            <a href="logout">Cerrar sesion</a>
        </nav>
    </div>
</header>

<main>

    <h2>Mis pedidos</h2>

    <c:if test="${not empty sessionScope.mensaje}">
        <div class="mensaje">
            <c:out value="${sessionScope.mensaje}" />
            <c:remove var="mensaje" scope="session" />
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty pedidos}">
            <c:forEach var="pedido" items="${pedidos}">
                <div class="pedido-card">
                    <div class="pedido-header">
                        <span>Pedido #${pedido.idPedido}</span>
                        <span>Fecha: ${pedido.fecha}</span>
                        <span>Total: <fmt:formatNumber value="${pedido.importe + pedido.iva}" pattern="#,##0.00" /> &euro;</span>
                    </div>
                    <table class="tabla-lineas">
                        <thead>
                            <tr>
                                <th>Producto</th>
                                <th>Cantidad</th>
                                <th>Precio</th>
                                <th>Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="linea" items="${pedido.lineas}">
                                <tr>
                                    <td>${linea.producto.nombre}</td>
                                    <td>${linea.cantidad}</td>
                                    <td><fmt:formatNumber value="${linea.producto.precio}" pattern="#,##0.00" /> &euro;</td>
                                    <td><fmt:formatNumber value="${linea.subtotal}" pattern="#,##0.00" /> &euro;</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <p>Todavia no tienes pedidos finalizados.</p>
        </c:otherwise>
    </c:choose>

    <a href="tienda" class="boton-escape">Volver a la tienda</a>

</main>

<footer>
    <p>&copy; GutiTech - Tienda de Informatica</p>
</footer>

</body>
</html>