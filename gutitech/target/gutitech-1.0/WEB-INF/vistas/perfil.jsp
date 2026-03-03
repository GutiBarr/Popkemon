<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>GutiTech - Mi Perfil</title>
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

    <h2>Mi perfil</h2>

    <c:if test="${not empty sessionScope.mensaje}">
        <div class="mensaje">
            <c:out value="${sessionScope.mensaje}" />
            <c:remove var="mensaje" scope="session" />
        </div>
    </c:if>

    <!-- Editar perfil -->
    <div class="formulario-contenedor">
        <h3>Editar datos personales</h3>

        <form method="post" action="perfil" enctype="multipart/form-data">
            <input type="hidden" name="accion" value="editarPerfil" />

            <label for="nombre">Nombre</label>
            <input type="text" id="nombre" name="nombre" value="${usuario.nombre}" required />

            <label for="apellidos">Apellidos</label>
            <input type="text" id="apellidos" name="apellidos" value="${usuario.apellidos}" required />

            <label for="telefono">Telefono</label>
            <input type="text" id="telefono" name="telefono" value="${usuario.telefono}" maxlength="9" />

            <label for="direccion">Direccion</label>
            <input type="text" id="direccion" name="direccion" value="${usuario.direccion}" required />

            <label for="codigoPostal">Codigo postal</label>
            <input type="text" id="codigoPostal" name="codigoPostal" value="${usuario.codigoPostal}" maxlength="5" required />

            <label for="localidad">Localidad</label>
            <input type="text" id="localidad" name="localidad" value="${usuario.localidad}" required />

            <label for="provincia">Provincia</label>
            <input type="text" id="provincia" name="provincia" value="${usuario.provincia}" required />

            <label for="avatar">Cambiar avatar</label>
            <c:if test="${not empty usuario.avatar}">
                <img src="media?archivo=${usuario.avatar}" alt="Avatar" class="avatar-preview" />
            </c:if>
            <input type="file" id="avatar" name="avatar" accept="image/jpeg,image/png,image/webp" />

            <button type="submit">Guardar cambios</button>
        </form>
    </div>

    <!-- Cambiar contrasena -->
    <div class="formulario-contenedor">
        <h3>Cambiar contrasena</h3>

        <form method="post" action="perfil">
            <input type="hidden" name="accion" value="cambiarPassword" />

            <label for="passwordActual">Contrasena actual</label>
            <input type="password" id="passwordActual" name="passwordActual" required />

            <label for="passwordNueva">Contrasena nueva</label>
            <input type="password" id="passwordNueva" name="passwordNueva" required />

            <label for="passwordRep">Repetir contrasena nueva</label>
            <input type="password" id="passwordRep" name="passwordRep" required />
            <span id="mensajePassword" class="aviso"></span>

            <button type="submit">Cambiar contrasena</button>
        </form>
    </div>

    <a href="tienda" class="boton-escape">Volver a la tienda</a>

</main>

<footer>
    <p>&copy; GutiTech - Tienda de Informatica</p>
</footer>

<script>
    document.getElementById("passwordRep").addEventListener("input", function() {
        var pass1 = document.getElementById("passwordNueva").value;
        var span = document.getElementById("mensajePassword");
        if (this.value !== pass1) {
            span.textContent = "Las contrasenas no coinciden";
            span.className = "aviso error";
        } else {
            span.textContent = "Las contrasenas coinciden";
            span.className = "aviso ok";
        }
    });
</script>

</body>
</html>