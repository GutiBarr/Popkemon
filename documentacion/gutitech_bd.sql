-- ============================================================
--  GutiTech - Script de creación y carga de base de datos
--  Base de datos : tienda_on_line
--  Motor         : MySQL 9 (Docker)
--  Codificación  : UTF-8
--  Autor         : José María Gutiérrez Barrena
-- ============================================================

-- ------------------------------------------------------------
-- 1. CREAR Y SELECCIONAR BASE DE DATOS
-- ------------------------------------------------------------
DROP DATABASE IF EXISTS tienda_on_line;

CREATE DATABASE tienda_on_line
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tienda_on_line;

-- ------------------------------------------------------------
-- 2. TABLA: categorias
-- ------------------------------------------------------------
CREATE TABLE categorias (
    IdCategoria  SMALLINT     NOT NULL AUTO_INCREMENT,
    Nombre       VARCHAR(50)  NOT NULL,
    Descripcion  TEXT,
    Imagen       VARCHAR(100),
    PRIMARY KEY (IdCategoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. TABLA: productos
-- ------------------------------------------------------------
CREATE TABLE productos (
    IdProducto   SMALLINT       NOT NULL AUTO_INCREMENT,
    IdCategoria  SMALLINT       NOT NULL,
    Nombre       VARCHAR(100)   NOT NULL,
    Marca        VARCHAR(50),
    Descripcion  TEXT,
    Precio       DECIMAL(6,2)   NOT NULL DEFAULT 0.00,
    Stock        SMALLINT       NOT NULL DEFAULT 0,
    Imagen       VARCHAR(100),
    Destacado    TINYINT(1)     NOT NULL DEFAULT 0,
    PRIMARY KEY  (IdProducto),
    CONSTRAINT fk_prod_cat FOREIGN KEY (IdCategoria)
        REFERENCES categorias (IdCategoria)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. TABLA: usuarios
-- ------------------------------------------------------------
CREATE TABLE usuarios (
    IdUsuario    SMALLINT     NOT NULL AUTO_INCREMENT,
    Email        VARCHAR(50)  NOT NULL,
    Password     VARCHAR(100) NOT NULL,
    Nombre       VARCHAR(20)  NOT NULL,
    Apellidos    VARCHAR(30),
    NIF          CHAR(9),
    Telefono     CHAR(9),
    Direccion    VARCHAR(40),
    CodigoPostal CHAR(5),
    Localidad    VARCHAR(40),
    Provincia    VARCHAR(30),
    UltimoAcceso DATETIME,
    Avatar       VARCHAR(100),
    PRIMARY KEY  (IdUsuario),
    UNIQUE KEY   uq_email (Email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. TABLA: pedidos
-- ------------------------------------------------------------
CREATE TABLE pedidos (
    IdPedido  SMALLINT      NOT NULL AUTO_INCREMENT,
    IdUsuario SMALLINT      NOT NULL,
    Fecha     DATE          NOT NULL DEFAULT (CURRENT_DATE),
    Estado    CHAR(1)       NOT NULL DEFAULT 'c',  -- c=carrito, f=finalizado
    Importe   DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    Iva       DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    PRIMARY KEY (IdPedido),
    CONSTRAINT fk_ped_usr FOREIGN KEY (IdUsuario)
        REFERENCES usuarios (IdUsuario)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 6. TABLA: lineaspedidos
-- ------------------------------------------------------------
CREATE TABLE lineaspedidos (
    IdLinea    SMALLINT        NOT NULL AUTO_INCREMENT,
    IdPedido   SMALLINT        NOT NULL,
    IdProducto SMALLINT        NOT NULL,
    Cantidad   TINYINT UNSIGNED NOT NULL DEFAULT 1,
    PRIMARY KEY (IdLinea),
    CONSTRAINT fk_lin_ped  FOREIGN KEY (IdPedido)
        REFERENCES pedidos (IdPedido)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lin_prod FOREIGN KEY (IdProducto)
        REFERENCES productos (IdProducto)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- CARGA DE DATOS
-- ============================================================

-- ------------------------------------------------------------
-- 7. CATEGORIAS (23 categorías)
-- ------------------------------------------------------------
INSERT INTO categorias (Nombre, Descripcion) VALUES
('Procesadores',        'CPUs Intel y AMD para escritorio y portátil'),
('Tarjetas Gráficas',   'GPUs NVIDIA y AMD para gaming y trabajo profesional'),
('Memorias RAM',        'Módulos DDR4 y DDR5 para equipos de sobremesa y portátil'),
('Almacenamiento SSD',  'Unidades de estado sólido SATA y NVMe'),
('Discos Duros HDD',    'Discos mecánicos de alta capacidad'),
('Placas Base',         'Motherboards ATX, mATX e ITX para Intel y AMD'),
('Fuentes de Alimentación', 'PSUs certificadas 80 Plus para todo tipo de builds'),
('Refrigeración',       'Coolers de aire y refrigeración líquida AIO'),
('Cajas PC',            'Torres ATX, mATX e ITX con diseño moderno'),
('Monitores',           'Pantallas Full HD, QHD y 4K para gaming y trabajo'),
('Teclados',            'Teclados mecánicos y membrana con y sin cable'),
('Ratones',             'Ratones gaming y de oficina ópticos y láser'),
('Auriculares',         'Auriculares gaming y estudio con y sin micrófono'),
('Webcams',             'Cámaras web Full HD y 4K para streaming y videollamadas'),
('Altavoces',           'Sistemas de sonido 2.0 y 2.1 para escritorio'),
('Redes',               'Tarjetas de red, routers, switches y adaptadores Wi-Fi'),
('Portátiles',          'Laptops para gaming, trabajo y uso diario'),
('Tablets',             'Tablets Android, iPad y Windows'),
('Impresoras',          'Impresoras de tinta, láser y multifunción'),
('Cables y Adaptadores','Cables HDMI, DisplayPort, USB y adaptadores varios'),
('Sillas Gaming',       'Sillas ergonómicas para largas sesiones de juego'),
('Escritorios',         'Escritorios gaming y de oficina regulables'),
('Software',            'Sistemas operativos, antivirus y paquetes de productividad');

-- ------------------------------------------------------------
-- 8. PRODUCTOS (45 productos de ejemplo)
-- ------------------------------------------------------------
INSERT INTO productos (IdCategoria, Nombre, Marca, Descripcion, Precio, Stock, Destacado) VALUES
-- Procesadores
(1, 'Intel Core i9-14900K', 'Intel', 'Procesador de 24 núcleos (8P+16E) hasta 6.0 GHz. Socket LGA1700. Para gaming y trabajo profesional.', 589.99, 15, 1),
(1, 'AMD Ryzen 9 7950X', 'AMD', 'Procesador de 16 núcleos y 32 hilos hasta 5.7 GHz. Socket AM5. Ideal para creación de contenido.', 649.99, 10, 1),
(1, 'Intel Core i5-13600K', 'Intel', 'Procesador de 14 núcleos (6P+8E) hasta 5.1 GHz. Socket LGA1700. Excelente relación calidad-precio.', 289.99, 25, 0),
(1, 'AMD Ryzen 5 7600X', 'AMD', 'Procesador de 6 núcleos y 12 hilos hasta 5.3 GHz. Socket AM5. Perfecto para gaming.', 219.99, 30, 0),
-- Tarjetas Gráficas
(2, 'NVIDIA RTX 4090', 'NVIDIA', 'La GPU más potente del mercado. 24 GB GDDR6X. Para gaming 4K y trabajo profesional.', 1899.99, 5, 1),
(2, 'AMD Radeon RX 7900 XTX', 'AMD', '24 GB GDDR6. Excelente para gaming 4K. Arquitectura RDNA 3.', 999.99, 8, 1),
(2, 'NVIDIA RTX 4070 Ti', 'NVIDIA', '12 GB GDDR6X. Perfecto para gaming QHD. Gran rendimiento por precio.', 799.99, 12, 0),
(2, 'AMD Radeon RX 7600', 'AMD', '8 GB GDDR6. Ideal para gaming Full HD. Bajo consumo.', 279.99, 20, 0),
-- Memorias RAM
(3, 'Corsair Vengeance DDR5 32GB', 'Corsair', 'Kit 2x16 GB DDR5 6000 MHz CL30. Ideal para plataformas AM5 e Intel 13/14 gen.', 149.99, 40, 0),
(3, 'G.Skill Trident Z5 64GB', 'G.Skill', 'Kit 2x32 GB DDR5 6400 MHz CL32. Para workstations y edición de vídeo.', 249.99, 15, 0),
(3, 'Kingston Fury Beast DDR4 16GB', 'Kingston', 'Kit 2x8 GB DDR4 3200 MHz CL16. Compatible con la mayoría de plataformas.', 49.99, 60, 0),
-- Almacenamiento SSD
(4, 'Samsung 990 Pro 2TB NVMe', 'Samsung', 'SSD NVMe PCIe 4.0 M.2. Velocidad lectura 7450 MB/s. Para sistemas y carga de juegos.', 189.99, 25, 1),
(4, 'WD Black SN850X 1TB', 'Western Digital', 'SSD NVMe PCIe 4.0 M.2. Lectura 7300 MB/s. Optimizado para gaming con PS5.', 119.99, 30, 0),
(4, 'Crucial MX500 2TB SATA', 'Crucial', 'SSD SATA 2.5". Lectura 560 MB/s. Ideal para almacenamiento secundario.', 129.99, 35, 0),
-- Discos Duros
(5, 'Seagate Barracuda 4TB', 'Seagate', 'HDD 3.5" 7200 RPM SATA. Para almacenamiento masivo de datos y copias de seguridad.', 79.99, 20, 0),
(5, 'WD Blue 2TB', 'Western Digital', 'HDD 3.5" 7200 RPM SATA. Fiable y silencioso para uso diario.', 54.99, 25, 0),
-- Placas Base
(6, 'ASUS ROG Strix Z790-E', 'ASUS', 'Placa base ATX para Intel LGA1700. Wi-Fi 6E, DDR5, PCIe 5.0. Para builds de alto rendimiento.', 499.99, 8, 1),
(6, 'MSI MAG B650 TOMAHAWK', 'MSI', 'Placa base ATX para AMD AM5. Wi-Fi 6E, DDR5. Relación calidad-precio excelente.', 239.99, 15, 0),
-- Fuentes de Alimentación
(7, 'Corsair RM1000x 1000W', 'Corsair', 'Fuente modular 80 Plus Gold 1000W. Para builds con RTX 4090. Silenciosa y eficiente.', 179.99, 10, 0),
(7, 'Seasonic Focus GX-750 750W', 'Seasonic', 'Fuente modular 80 Plus Gold 750W. Ideal para la mayoría de configuraciones gaming.', 119.99, 20, 0),
-- Refrigeración
(8, 'Noctua NH-D15 Chromax', 'Noctua', 'Cooler de aire de doble torre. El mejor rendimiento sin líquido. Compatible AM5/LGA1700.', 99.99, 15, 0),
(8, 'NZXT Kraken 360 RGB', 'NZXT', 'Refrigeración líquida AIO 360 mm. Bomba mejorada, pantalla LCD. Para CPUs de alta gama.', 199.99, 10, 1),
-- Cajas PC
(9, 'Fractal Design Torrent', 'Fractal Design', 'Torre ATX con enorme flujo de aire. Soporte para refrigeración líquida de hasta 420 mm.', 169.99, 8, 0),
(9, 'NZXT H7 Flow', 'NZXT', 'Torre ATX minimalista con excelente gestión de cables. Panel lateral de cristal.', 129.99, 12, 0),
-- Monitores
(10, 'LG 27GP950-B 4K 144Hz', 'LG', 'Monitor 27" 4K UHD 144Hz IPS. HDMI 2.1, DisplayPort 1.4. Ideal para gaming y trabajo.', 699.99, 6, 1),
(10, 'Samsung Odyssey G5 27" QHD', 'Samsung', 'Monitor curvo 27" QHD 165Hz VA. 1ms. Perfecto para gaming inmersivo.', 299.99, 15, 0),
-- Teclados
(11, 'Logitech G Pro X TKL', 'Logitech', 'Teclado mecánico TKL sin cable. Switches GX. Diseño para esports.', 149.99, 20, 0),
(11, 'Keychron K2 Pro', 'Keychron', 'Teclado mecánico 75% Bluetooth/USB. Hotswap. Compatible Mac y Windows.', 99.99, 25, 0),
-- Ratones
(12, 'Logitech G Pro X Superlight 2', 'Logitech', 'Ratón gaming ultraligero 60g sin cable. Sensor HERO 2 25K. Para esports.', 159.99, 18, 1),
(12, 'Razer DeathAdder V3', 'Razer', 'Ratón gaming ergonómico con cable. Sensor Focus Pro 30K. 59g.', 79.99, 22, 0),
-- Auriculares
(13, 'SteelSeries Arctis Nova Pro', 'SteelSeries', 'Auriculares gaming con cancelación activa de ruido. Hi-Fi. Multiplataforma.', 349.99, 8, 1),
(13, 'HyperX Cloud III', 'HyperX', 'Auriculares gaming con cable. Micrófono desmontable. Sonido DTS. Muy cómodos.', 99.99, 20, 0),
-- Webcams
(14, 'Logitech StreamCam', 'Logitech', 'Webcam Full HD 60fps con autoenfoque inteligente. Para streaming y videollamadas.', 149.99, 12, 0),
-- Redes
(16, 'ASUS PCE-AX58BT Wi-Fi 6', 'ASUS', 'Tarjeta de red PCIe Wi-Fi 6 AX3000. Bluetooth 5.0. Para escritorio.', 69.99, 20, 0),
(16, 'TP-Link TL-SG108 Switch 8p', 'TP-Link', 'Switch Gigabit de 8 puertos. Plug and play. Para redes domésticas y de oficina.', 24.99, 35, 0),
-- Portátiles
(17, 'ASUS ROG Zephyrus G14', 'ASUS', 'Portátil gaming 14" QHD 165Hz. AMD Ryzen 9 7940HS, RTX 4060, 16GB DDR5, 1TB NVMe.', 1499.99, 5, 1),
(17, 'Lenovo ThinkPad X1 Carbon', 'Lenovo', 'Portátil empresarial 14" WUXGA. Intel Core i7-1365U, 16GB LPDDR5, 512GB NVMe. Ligero.', 1299.99, 6, 0),
-- Cables
(20, 'Cable HDMI 2.1 2m', 'Baseus', 'Cable HDMI 2.1 certificado 48 Gbps. Soporta 4K 144Hz y 8K 60Hz.', 14.99, 50, 0),
(20, 'Hub USB-C 10 en 1', 'Anker', 'Hub USB-C con HDMI 4K, USB 3.0 x3, SD/microSD, USB-C PD 100W, Ethernet.', 49.99, 30, 0),
-- Sillas Gaming
(21, 'SecretLab Titan Evo 2022', 'SecretLab', 'Silla gaming ergonómica con soporte lumbar magnético. Tela SoftWeave Plus. Duradera.', 449.99, 6, 1),
(21, 'DXRacer Formula F08', 'DXRacer', 'Silla gaming clásica con reposacabezas y cojín lumbar. Disponible en varios colores.', 299.99, 10, 0),
-- Software
(23, 'Windows 11 Pro OEM', 'Microsoft', 'Licencia OEM de Windows 11 Professional. Para un equipo. Sin caducidad.', 129.99, 100, 0),
(23, 'Microsoft Office 2021 Home', 'Microsoft', 'Paquete Office 2021 para uso doméstico. Word, Excel, PowerPoint y OneNote. Un PC.', 149.99, 100, 0);

-- ------------------------------------------------------------
-- 9. USUARIO DE PRUEBA
--    Password: Admin1234!
--    Hash Argon2id generado con la propia aplicación
-- ------------------------------------------------------------
INSERT INTO usuarios (Email, Password, Nombre, Apellidos, NIF, Telefono, Direccion, CodigoPostal, Localidad, Provincia, UltimoAcceso) VALUES
('admin@gutitech.com',
 '$argon2id$v=19$m=65536,t=2,p=1$exampleSaltBase64Here$exampleHashBase64HereReplace',
 'Admin', 'GutiTech', '00000000T', '600000000',
 'Calle Falsa 123', '06800', 'Mérida', 'Badajoz', NOW());

-- Nota: sustituir el hash anterior por el generado por la aplicación al registrar el usuario.

-- ------------------------------------------------------------
-- 10. VERIFICACIÓN FINAL
-- ------------------------------------------------------------
SELECT 'categorias'   AS Tabla, COUNT(*) AS Registros FROM categorias
UNION ALL
SELECT 'productos',   COUNT(*) FROM productos
UNION ALL
SELECT 'usuarios',    COUNT(*) FROM usuarios
UNION ALL
SELECT 'pedidos',     COUNT(*) FROM pedidos
UNION ALL
SELECT 'lineaspedidos', COUNT(*) FROM lineaspedidos;
