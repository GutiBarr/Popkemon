<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>GutiTech - Tienda</title>
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
                    <a href="perfil" class="nav-usuario">
    <c:if test="${not empty sessionScope.usuario.avatar}">
        <img src="media/${sessionScope.usuario.avatar}" class="avatar-nav" />
    </c:if>
    ${sessionScope.usuario.nombre}
</a>
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
    <form method="get" action="tienda" id="formFiltros">

        <!-- Busqueda -->
        <div class="filtro-fila">
            <input type="text" name="busqueda" placeholder="Buscar producto..."
                   value="${not empty busqueda ? busqueda : ''}" />
            <button type="submit">Buscar</button>
            <a href="tienda">Ver todos</a>
        </div>

        <!-- Marca y ordenacion -->
        <div class="filtro-fila">
            <select name="marca" onchange="document.getElementById('formFiltros').submit()">
                <option value="">Todas las marcas</option>
                <c:forEach var="m" items="${marcas}">
                    <option value="${m}" ${marca == m ? 'selected' : ''}>${m}</option>
                </c:forEach>
            </select>

            <select name="orden" onchange="document.getElementById('formFiltros').submit()">
                <option value="">Ordenar por</option>
                <option value="nombre" ${orden == 'nombre' ? 'selected' : ''}>Nombre A-Z</option>
                <option value="precioAsc" ${orden == 'precioAsc' ? 'selected' : ''}>Precio menor a mayor</option>
                <option value="precioDesc" ${orden == 'precioDesc' ? 'selected' : ''}>Precio mayor a menor</option>
            </select>
        </div>

        <!-- Rango de precio -->
        <div class="filtro-fila">
            <label>Precio:</label>
            <input type="range" id="sliderMin" name="precioMin" min="0" max="2000" step="10"
                   value="${not empty precioMin ? precioMin : 0}"
                   oninput="document.getElementById('valorMin').textContent = this.value" />
            <span id="valorMin">${not empty precioMin ? precioMin : 0}</span> &euro;

            <input type="range" id="sliderMax" name="precioMax" min="0" max="2000" step="10"
                   value="${not empty precioMax ? precioMax : 2000}"
                   oninput="document.getElementById('valorMax').textContent = this.value" />
            <span id="valorMax">${not empty precioMax ? precioMax : 2000}</span> &euro;

            <button type="submit">Aplicar precio</button>
        </div>

    </form>

    <!-- Categorias -->
    <div class="categorias-filtro">
        <c:forEach var="cat" items="${applicationScope.categorias}">
            <a href="tienda?idCategoria=${cat.idCategoria}"
               class="cat-card ${idCategoria == cat.idCategoria ? 'activo' : ''}">
                <img src="imagenes/categorias/${cat.imagen}" alt="${cat.nombre}" />
                <span>${cat.nombre}</span>
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
                            <img src="imagenes/${p.imagen}.jpg" alt="${p.nombre}" />
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
    <p>&copy; GutiTech - Tienda de Informatica</p>
</footer>

</body>
</html>