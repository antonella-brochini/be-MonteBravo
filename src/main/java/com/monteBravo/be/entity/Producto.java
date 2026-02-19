    package com.monteBravo.be.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;

    import java.util.List;
    import java.util.Objects;


    @Entity
    @Table(name = "productos")
    public class Producto {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long idProducto;
        @Column(unique = true)
        private String nombre;
        private double precio;
        private String descripcion;
        private String imagen;
        private int stockActual;
        private int stockInicial;
        private int porcentajeAlerta;
        private boolean deshabilitadoPorStock;

        @Column(unique = true)
        private String sku;
        @ManyToOne
        @JoinColumn(name = "idCategoriaProducto")
        private CategoriaProducto categoriaProducto;
        @JsonIgnore
        @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
        private List<CarritoProducto> carritos;

        public Producto(String nombre, double precio, String descripcion, String imagen, int stockActual, String sku, CategoriaProducto categoriaProducto, int porcentajeAlerta) {

            this.nombre = nombre;
            this.precio = precio;
            this.descripcion = descripcion;
            this.imagen = imagen;
            this.stockActual = stockActual;
            this.stockInicial = stockActual;
            this.porcentajeAlerta = porcentajeAlerta;
            this.sku = sku;
            this.categoriaProducto = categoriaProducto;
            this.deshabilitadoPorStock = false;
        }

        public Producto() {
        }

        public int getStockInicial() {
            return stockInicial;
        }

        public void setStockInicial(int stockInicial) {
            this.stockInicial = stockInicial;
        }

        public long getIdProducto() {
            return idProducto;
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

        public CategoriaProducto getCategoriaProducto() {
            return categoriaProducto;
        }

        public void setCategoriaProducto(CategoriaProducto categoriaProducto) {
            this.categoriaProducto = categoriaProducto;
        }

        public List<CarritoProducto> getCarritos() {
            return carritos;
        }

        public void setCarritos(List<CarritoProducto> carritos) {
            this.carritos = carritos;
        }

        public int getPorcentajeAlerta() {
            return porcentajeAlerta;
        }

        public void setPorcentajeAlerta(int porcentajeAlerta) {
            this.porcentajeAlerta = porcentajeAlerta;
        }

        public boolean isDeshabilitadoPorStock() {
            return deshabilitadoPorStock;
        }

        public void setDeshabilitadoPorStock(boolean deshabilitadoPorStock) {
            this.deshabilitadoPorStock = deshabilitadoPorStock;
        }

        @Override
        public boolean equals(Object o) {

            Producto producto = (Producto) o;
            return idProducto == producto.getIdProducto();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(idProducto);
        }

        public Boolean puedoMoverStock(int cantidad, int signo) {
            if (signo == 1) {
                return true;
            } else if (signo == 0 && stockActual - cantidad >= 0) {
                return true;
            }
            return false;

        }

        public Boolean moverStock(int cantidad, int signo){
            if (signo==1) {
                stockActual = cantidad+stockActual;
                return true;
            } else if (signo == 0 && stockActual - cantidad >= 0) {
                stockActual = stockActual - cantidad;
                return true;
            }
            return false;
        }

        public void actualizarStockInicial(int signo) {
            // Rango de tolerancia (±20% del stock inicial)
            double limiteSuperior = this.getStockInicial() * 1.20;
            double limiteInferior = this.getStockInicial() * 0.80;
            //Si el sigmo del cambio del stock es positivo
            if (signo>0 && (stockActual > limiteSuperior || stockActual < limiteInferior)) {
                this.setStockInicial(stockActual);
            }
        }

        public boolean esStockBajo() {
            double limiteAlerta = this.stockInicial * (this.porcentajeAlerta / 100.0);
            return this.stockActual < limiteAlerta;
        }
    }


