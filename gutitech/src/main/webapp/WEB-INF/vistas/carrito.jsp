<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>GutiTech - Carrito</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

<header>
    <div class="header-contenido">
        <a href="tienda"><h1>GutiTech</h1></a>
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

    <h2>Mi carrito</h2>

    <c:if test="${not empty sessionScope.mensaje}">
        <div class="mensaje">
            <c:out value="${sessionScope.mensaje}" />
            <c:remove var="mensaje" scope="session" />
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty carrito and not empty carrito.lineas}">

            <table class="tabla-carrito">
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Precio</th>
                        <th>Cantidad</th>
                        <th>Subtotal</th>
                        <th>Eliminar</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="linea" items="${carrito.lineas}">
                        <tr id="linea-${linea.idLinea}">
                            <td>
                                <a href="producto?id=${linea.producto.idProducto}">
                                    ${linea.producto.nombre}
                                </a>
                            </td>
                            <td>
                                <fmt:formatNumber value="${linea.producto.precio}" pattern="#,##0.00" /> &euro;
                            </td>
                            <td>
                                <button class="btn-cantidad"
                                        onclick="cambiarCantidad(${linea.idLinea}, ${linea.cantidad}, 'disminuir')">-</button>
                                <span id="cantidad-${linea.idLinea}">${linea.cantidad}</span>
                                <button class="btn-cantidad"
                                        onclick="cambiarCantidad(${linea.idLinea}, ${linea.cantidad}, 'aumentar')">+</button>
                            </td>
                            <td id="subtotal-${linea.idLinea}">
                                <fmt:formatNumber value="${linea.subtotal}" pattern="#,##0.00" /> &euro;
                            </td>
                            <td>
                                <form method="post" action="carrito">
                                    <input type="hidden" name="accion" value="eliminarLinea" />
                                    <input type="hidden" name="idLinea" value="${linea.idLinea}" />
                                    <button type="submit" class="btn-eliminar">X</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="carrito-resumen">
                <p>Total (sin IVA): <span id="totalSinIva"><fmt:formatNumber value="${carrito.importe}" pattern="#,##0.00" /></span> &euro;</p>
                <p>IVA (21%): <fmt:formatNumber value="${carrito.iva}" pattern="#,##0.00" /> &euro;</p>
                <p><strong>Total: <fmt:formatNumber value="${carrito.importe + carrito.iva}" pattern="#,##0.00" /> &euro;</strong></p>

                <form method="post" action="carrito">
                    <input type="hidden" name="accion" value="finalizar" />
                    <button type="submit" class="btn-finalizar">Finalizar pedido</button>
                </form>

                <form method="post" action="carrito">
                    <input type="hidden" name="accion" value="vaciar" />
                    <button type="submit" class="btn-vaciar">Vaciar carrito</button>
                </form>
            </div>

        </c:when>
        <c:otherwise>
            <p>Tu carrito esta vacio.</p>
        </c:otherwise>
    </c:choose>

    <a href="tienda" class="boton-escape">Seguir comprando</a>

</main>

<footer>
    <p>&copy; GutiTech - Tienda de Informatica</p>
</footer>

<script>
    function cambiarCantidad(idLinea, cantidad, accion) {
        fetch("ajax?accion=" + accion, {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: "accion=" + accion + "&idLinea=" + idLinea + "&cantidad=" + cantidad
        })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            document.getElementById("cantidad-" + idLinea).textContent = data.cantidad;
            document.getElementById("totalSinIva").textContent = data.total.toFixed(2);
        });
    }
</script>

</body>
</html>