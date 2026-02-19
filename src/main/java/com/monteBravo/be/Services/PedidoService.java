package com.monteBravo.be.Services;

import com.monteBravo.be.DTO.PedidoProductoDTO;
import com.monteBravo.be.Repository.ClienteRepository;
import com.monteBravo.be.Repository.PedidoProductoRepository;
import com.monteBravo.be.Repository.PedidoRepository;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.entity.Pedido;
import com.monteBravo.be.entity.PedidoProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private  ClienteRepository clienteRepository;
    @Autowired
    private  PedidoProductoRepository pedidoProductoRepository;

    public Pedido crearPedido(Pedido pedido){
        pedido.setFechaCompra(new Date());
        Pedido p = pedidoRepository.save(pedido); // Se guarda primero el pedido para que grabe el ID
        for (PedidoProducto pedidoProducto : pedido.getPedidosProducto()) {
            pedidoProducto.setPedido(pedido);
        } // este for es para que persista el ID del pedido en PedidoProducto
        pedidoProductoRepository.saveAll(pedido.getPedidosProducto());
        return p;
    }


    public List<Pedido> findAllPedidos (){
        return pedidoRepository.findAll();
    }


    public Optional<Pedido> findById(Long id ){
        return pedidoRepository.findById(id);

    }

    public List<Pedido> findByClientId(Long idUser){
        return pedidoRepository.findAllByClienteId(idUser);

    }

    public  List<PedidoProductoDTO> findPedidoProductoByPedidoId(Long id){
        return pedidoProductoRepository.findPedidoProductosByPedidoId(id);

    }


    public List<Pedido> todosDecendente(){
       return  pedidoRepository.findAllDecendente();

    }
}
