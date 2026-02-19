    package com.monteBravo.be.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;

    import java.util.Date;
    import java.util.List;

    @Entity
    @Table(name = "pedido")
    public class Pedido {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long idPedido;
        private String idMercadoPago;
        @ManyToOne(optional = true)
        @JoinColumn(name = "idUser", nullable = true)
        private Cliente cliente;
        @OneToMany(mappedBy = "pedido", cascade = CascadeType.PERSIST)
        List<PedidoProducto> pedidosProducto;
        private Date fechaCompra;

        private String email;
        private String celular;

        private boolean localPickUp;
        private String metodoEnvio;
        private Double costo;
//direccion
        private String state;
        private String city;
        private String postalCode;
        private String street;
        private String streetNumber;
        private String aclaracion;

        public Pedido(String aclaracion, String streetNumber, Cliente cliente, List<PedidoProducto> pedidosProducto, Date fechaCompra, String email, String celular, boolean localPickUp, String metodoEnvio, Double costo, String state, String city, String postalCode, String street) {
            this.aclaracion = aclaracion;
            this.streetNumber = streetNumber;
            this.cliente = cliente;
            this.pedidosProducto = pedidosProducto;
            this.fechaCompra = fechaCompra;
            this.email = email;
            this.celular = celular;
            this.localPickUp = localPickUp;
            this.metodoEnvio = metodoEnvio;
            this.costo = costo;
            this.state = state;
            this.city = city;
            this.postalCode = postalCode;
            this.street = street;
        }

        public Date getFechaCompra() {
            return fechaCompra;
        }

        public void setFechaCompra(Date fechaCompra) {
            this.fechaCompra = fechaCompra;
        }

        public Pedido() {}

        public Cliente getCliente() {
            return cliente;
        }

        public void setCliente(Cliente cliente) {
            this.cliente = cliente;
        }

        public long getIdPedido() {
            return idPedido;
        }

        public List<PedidoProducto> getPedidosProducto() {
            return pedidosProducto;
        }

        public void setPedidosProducto(List<PedidoProducto> pedidosProducto) {
            this.pedidosProducto = pedidosProducto;
        }

        public String getEmail() { return email;}
        public void setEmail(String email) { this.email = email; }

        public void setIdPedido(long idPedido) {
            this.idPedido = idPedido;
        }

        public String getAclaracion() {
            return aclaracion;
        }

        public void setAclaracion(String aclaracion) {
            this.aclaracion = aclaracion;
        }

        public String getStreetNumber() {
            return streetNumber;
        }

        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Double getCosto() {
            return costo;
        }

        public void setCosto(Double costo) {
            this.costo = costo;
        }

        public String getEnvio() {
            return metodoEnvio;
        }

        public void setEnvio(String envio) {
            this.metodoEnvio = envio;
        }

        public boolean isLocalPickUp() {
            return localPickUp;
        }

        public void setLocalPickUp(boolean localPickUp) {
            this.localPickUp = localPickUp;
        }

        public String getCelular() {
            return celular;
        }

        public void setCelular(String celular) {
            this.celular = celular;
        }

        public String getIdMercadoPago() {
            return idMercadoPago;
        }

        public void setIdMercadoPago(String idMercadoPago) {
            this.idMercadoPago = idMercadoPago;
        }

        public String getMetodoEnvio() {
            return metodoEnvio;
        }

        public void setMetodoEnvio(String metodoEnvio) {
            this.metodoEnvio = metodoEnvio;
        }
    }
