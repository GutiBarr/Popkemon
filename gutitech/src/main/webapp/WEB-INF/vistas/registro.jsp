<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>GutiTech - Registro</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

<header>
    <div class="header-contenido">
        <a href="tienda"><h1>GutiTech</h1></a>
        <nav>
            <a href="tienda">Tienda</a>
            <a href="login">Iniciar sesion</a>
        </nav>
    </div>
</header>

<main>

    <div class="formulario-contenedor">
        <h2>Crear cuenta</h2>

        <c:if test="${not empty sessionScope.mensaje}">
            <div class="mensaje error">
                <c:out value="${sessionScope.mensaje}" />
                <c:remove var="mensaje" scope="session" />
            </div>
        </c:if>

        <form method="post" action="registro" enctype="multipart/form-data" id="formRegistro">

            <h3>Datos de acceso</h3>

            <label for="email">Email *</label>
            <input type="email" id="email" name="email" required />
            <span id="mensajeEmail" class="aviso"></span>

            <label for="password">Contrasena *</label>
            <input type="password" id="password" name="password" required />

            <label for="passwordRep">Repetir contrasena *</label>
            <input type="password" id="passwordRep" name="passwordRep" required />
            <span id="mensajePassword" class="aviso"></span>

            <h3>Datos personales</h3>

            <label for="nombre">Nombre *</label>
            <input type="text" id="nombre" name="nombre" required />

            <label for="apellidos">Apellidos *</label>
            <input type="text" id="apellidos" name="apellidos" required />

            <label for="nif">NIF *</label>
            <input type="text" id="nif" name="nif" maxlength="9" required />
            <span id="mensajeNif" class="aviso"></span>

            <label for="telefono">Telefono</label>
            <input type="text" id="telefono" name="telefono" maxlength="9" />

            <h3>Direccion</h3>

            <label for="direccion">Direccion *</label>
            <input type="text" id="direccion" name="direccion" required />

            <label for="codigoPostal">Codigo postal *</label>
            <input type="text" id="codigoPostal" name="codigoPostal" maxlength="5" required />
            <span id="mensajeCP" class="aviso"></span>

            <label for="localidad">Localidad *</label>
            <input type="text" id="localidad" name="localidad" required />

            <label for="provincia">Provincia *</label>
            <input type="text" id="provincia" name="provincia" required />

            <h3>Avatar</h3>

            <label for="avatar">Foto de perfil (opcional)</label>
            <input type="file" id="avatar" name="avatar" accept="image/jpeg,image/png,image/webp" />

            <button type="submit">Crear cuenta</button>
        </form>

        <a href="tienda" class="boton-escape">Volver a la tienda</a>
    </div>

</main>

<footer>
    <p>&copy; GutiTech - Tienda de Informatica</p>
</footer>

<script>
    // Comprobar email con Ajax
    document.getElementById("email").addEventListener("blur", function() {
        var email = this.value;
        if (email === "") return;

        fetch("ajax?accion=comprobarEmail&email=" + encodeURIComponent(email))
            .then(function(response) { return response.json(); })
            .then(function(data) {
                var span = document.getElementById("mensajeEmail");
                if (data.existe) {
                    span.textContent = "Este email ya esta registrado";
                    span.className = "aviso error";
                } else {
                    span.textContent = "Email disponible";
                    span.className = "aviso ok";
                }
            });
    });

    // Comprobar que las contrasenas coinciden
    document.getElementById("passwordRep").addEventListener("input", function() {
        var pass1 = document.getElementById("password").value;
        var span = document.getElementById("mensajePassword");
        if (this.value !== pass1) {
            span.textContent = "Las contrasenas no coinciden";
            span.className = "aviso error";
        } else {
            span.textContent = "Las contrasenas coinciden";
            span.className = "aviso ok";
        }
    });

    // Comprobar codigo postal (5 digitos)
    document.getElementById("codigoPostal").addEventListener("blur", function() {
        var span = document.getElementById("mensajeCP");
        if (!/^\d{5}$/.test(this.value)) {
            span.textContent = "El codigo postal debe tener 5 digitos";
            span.className = "aviso error";
        } else {
            span.textContent = "";
        }
    });

    // Calcular letra del NIF con Ajax
    document.getElementById("nif").addEventListener("blur", function() {
        var numeros = this.value.replace(/\D/g, "");
        if (numeros.length !== 8) return;
        var input = this;

        fetch("ajax?accion=calcularLetraNif&numeros=" + numeros)
            .then(function(response) { return response.json(); })
            .then(function(data) {
                if (data.letra) {
                    input.value = numeros + data.letra;
                    document.getElementById("mensajeNif").textContent = "Letra asignada: " + data.letra;
                    document.getElementById("mensajeNif").className = "aviso ok";
                }
            });
    });
</script>

</body>
</html>