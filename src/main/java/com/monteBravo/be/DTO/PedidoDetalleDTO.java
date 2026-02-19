package com.monteBravo.be.DTO;

import com.monteBravo.be.entity.Pedido;
import com.monteBravo.be.entity.PedidoProducto;

import java.util.List;

public class PedidoDetalleDTO {

    private PedidoDTO pedido;
    private List<PedidoProductoDTO> pedidoProductoList;

    public PedidoDetalleDTO(PedidoDTO pedido, List<PedidoProductoDTO> pedidoProductoList) {
        this.pedido = pedido;
        this.pedidoProductoList = pedidoProductoList;
    }
    public PedidoDetalleDTO(){}

    public PedidoDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDTO pedido) {
        this.pedido = pedido;
    }

    public List<PedidoProductoDTO> getPedidoProductoList() {
        return pedidoProductoList;
    }

    public void setPedidoProductoList(List<PedidoProductoDTO> pedidoProductoList) {
        this.pedidoProductoList = pedidoProductoList;
    }
}
