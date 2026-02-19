package com.monteBravo.be.DTO;

public class LikesProductosDTO {

    private Long userId;
    private boolean liked;
    private Long idProducto;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagen;
    private int stockActual;
    private String sku;
    private Long idCategoria;

    public LikesProductosDTO(Long userId, boolean liked, Long idProducto, String nombre, double precio,
                            String descripcion, String imagen, int stockActual, String sku) {
        this.userId = userId;
        this.liked = liked;
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.stockActual = stockActual;
        this.sku = sku;
    }

    /*sin USERiD*/
    public LikesProductosDTO( boolean liked, Long idProducto, String nombre, double precio,
                              String descripcion, String imagen, int stockActual, String sku, Long idCategoria) {
        this.liked = liked;
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.stockActual = stockActual;
        this.sku = sku;
        this.idCategoria = idCategoria;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
