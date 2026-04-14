BEGIN;

-- =========================================================
-- 1) reporte_vta
-- columnas:
-- nombre_usuario, id_pedido, cantidad, precio,
-- id_producto, nombre_producto, fecha_compra, categoria
-- origen: pedido + ped_productos + productos + usuario
-- =========================================================
DROP VIEW IF EXISTS reporte_vta;

CREATE OR REPLACE VIEW reporte_vta AS
SELECT
    u.nombre_usuario                                        AS nombre_usuario,
    pe.id_pedido                                            AS id_pedido,
    pp.cantidad                                             AS cantidad,
    pp.precio                                               AS precio,
    pr.id_producto                                          AS id_producto,
    pr.nombre                                               AS nombre_producto,
    pe.fecha_compra                                         AS fecha_compra,
    pr.id_categoria_producto                                AS categoria
FROM pedido pe
JOIN ped_productos pp
    ON pp.id_pedido = pe.id_pedido
JOIN productos pr
    ON pr.id_producto = pp.id_producto
LEFT JOIN usuario u
    ON u.id_user = pe.id_user;


-- =========================================================
-- 2) productos_mas_vendidos
-- columnas:
-- nombre_producto, total_cantidad, total_ingresos,
-- fecha_compra, categoria, id_producto
-- origen: ped_productos + productos + pedido
-- =========================================================
DROP VIEW IF EXISTS productos_mas_vendidos;

CREATE OR REPLACE VIEW productos_mas_vendidos AS
SELECT
    pr.nombre                                               AS nombre_producto,
    SUM(pp.cantidad)                                        AS total_cantidad,
    SUM(pp.cantidad * pp.precio)                            AS total_ingresos,
    pe.fecha_compra                                         AS fecha_compra,
    pr.id_categoria_producto                                AS categoria,
    pr.id_producto                                          AS id_producto
FROM ped_productos pp
JOIN productos pr
    ON pr.id_producto = pp.id_producto
JOIN pedido pe
    ON pe.id_pedido = pp.id_pedido
GROUP BY
    pr.nombre,
    pe.fecha_compra,
    pr.id_categoria_producto,
    pr.id_producto;


-- =========================================================
-- 3) reporte_clientes_compras
-- columnas:
-- id_cliente, nombre_cliente, total_compras, total_gasto
-- origen: pedido + usuario/cliente (+ ped_productos para gasto)
-- =========================================================
DROP VIEW IF EXISTS reporte_clientes_compras;

CREATE OR REPLACE VIEW reporte_clientes_compras AS
SELECT
    u.id_user                                               AS id_cliente,
    u.nombre_usuario                                        AS nombre_cliente,
    COUNT(DISTINCT pe.id_pedido)                            AS total_compras,
    COALESCE(SUM(pp.cantidad * pp.precio), 0)               AS total_gasto
FROM pedido pe
JOIN usuario u
    ON u.id_user = pe.id_user
LEFT JOIN ped_productos pp
    ON pp.id_pedido = pe.id_pedido
GROUP BY
    u.id_user,
    u.nombre_usuario;


-- =========================================================
-- 4) carrito_abandonado
-- columnas:
-- id_carrito, id_user, ultima_interaccion,
-- nombre_usuario, email, total
-- origen: carrito + usuario (+ carrito_producto para total)
-- =========================================================
DROP VIEW IF EXISTS carrito_abandonado;

CREATE OR REPLACE VIEW carrito_abandonado AS
SELECT
    c.id_carrito                                            AS id_carrito,
    u.id_user                                               AS id_user,
    c.ultima_interaccion                                    AS ultima_interaccion,
    u.nombre_usuario                                        AS nombre_usuario,
    u.email                                                 AS email,
    COALESCE(SUM(cp.cantidad * cp.precio), 0)               AS total
FROM carrito c
LEFT JOIN usuario u
    ON u.id_user = c.id_user
LEFT JOIN carrito_producto cp
    ON cp.id_carrito = c.id_carrito
GROUP BY
    c.id_carrito,
    u.id_user,
    c.ultima_interaccion,
    u.nombre_usuario,
    u.email;

COMMIT;