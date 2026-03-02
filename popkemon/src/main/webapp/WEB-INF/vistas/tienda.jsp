<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Popkemon - Tienda</title>
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

    <!-- Mensaje flash -->
    <c:if test="${not empty sessionScope.mensaje}">
        <div class="mensaje">
            <c:out value="${sessionScope.mensaje}" />
            <c:remove var="mensaje" scope="session" />
        </div>
    </c:if>

    <!-- Filtros -->
    <div class="filtros">
        <form method="get" action="tienda">
            <input type="text" name="busqueda" placeholder="Buscar Funko Pop..."
                   value="${not empty busqueda ? busqueda : ''}" />
            <button type="submit">Buscar</button>
            <a href="tienda">Ver todos</a>
        </form>

        <div class="categorias-filtro">
            <c:forEach var="cat" items="${applicationScope.categorias}">
                <a href="tienda?idCategoria=${cat.idCategoria}"
                   class="${idCategoria == cat.idCategoria ? 'activo' : ''}">
                    ${cat.nombre}
                </a>
            </c:forEach>
        </div>
    </div>

    <!-- Productos -->
    <div class="grid-productos">
        <c:choose>
            <c:when test="${not empty productos}">
                <c:forEach var="p" items="${productos}">
                    <div class="card-producto">
                        <a href="producto?id=${p.idProducto}">
                            <img src="imagenes/${p.imagen}" alt="${p.nombre}" />
                            <h3>${p.nombre}</h3>
                            <p class="marca">${p.marca}</p>
                            <p class="precio"><fmt:formatNumber value="${p.precio}" pattern="#,##0.00" /> &euro;</p>
                        </a>
                        <form method="post" action="carrito">
                            <input type="hidden" name="accion" value="anadir" />
                            <input type="hidden" name="idProducto" value="${p.idProducto}" />
                            <input type="hidden" name="cantidad" value="1" />
                            <button type="submit">Añadir al carrito</button>
                        </form>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p>No se han encontrado productos.</p>
            </c:otherwise>
        </c:choose>
    </div>

</main>

<footer>
    <p>&copy; Popkemon - Tienda de Funko Pops Pokemon</p>
</footer>

</body>
</html>