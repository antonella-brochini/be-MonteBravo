package com.monteBravo.be.Services;

import com.monteBravo.be.DTO.StockDTO;
import com.monteBravo.be.Repository.AdministradorRepository;
import com.monteBravo.be.Repository.ClienteRepository;
import com.monteBravo.be.Repository.MovStockRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.Services.Impl.AlertaStockServiceImpl;
import com.monteBravo.be.entity.Administrador;
import com.monteBravo.be.entity.Exception.StockException;
import com.monteBravo.be.entity.MovimientoStock;
import com.monteBravo.be.entity.Producto;
import com.monteBravo.be.entity.Usuario;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovStockService {

    private final MovStockRepository movStockRepository;
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    AlertaStockServiceImpl alertaStockServiceImpl;

    public MovStockService(MovStockRepository movStockRepository, ClienteRepository clienteRepository, AdministradorRepository administradorRepository, ProductoRepository productoRepository) {
        this.movStockRepository = movStockRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.productoRepository = productoRepository;
    }

    public MovimientoStock crear(StockDTO stock) throws BadRequestException{
        Usuario usuario = null;
        usuario  = administradorRepository.findById(stock.getIdUser()).orElse(null);

        if(usuario == null) throw new BadRequestException("No existe un usuario con ID: " + stock.getIdUser());

        Producto producto = productoRepository.findById(stock.getIdProducto()).orElse(null);

        if (producto == null) throw new BadRequestException("Producto no encontrado");

        Boolean ok = producto.moverStock(stock.getCantidad(), stock.getSigno());
        if(!ok)
             throw new BadRequestException("Ingreso una cantidad mayor al stock actual");

        // Obtener la fecha y hora del sistema
        LocalDateTime ahora = LocalDateTime.now();
        // Definir la zona horaria de Uruguay
        ZoneId zona = ZoneId.of("America/Montevideo");
        // Obtener la fecha y hora actual en la zona de Uruguay
        ZonedDateTime fechaConZona = ahora.atZone(zona);

        MovimientoStock movimientoStock = new MovimientoStock(usuario, producto,
                stock.getCantidad(),
                fechaConZona.toLocalDateTime(),
                stock.getMotivo(),
                stock.getSigno(), producto.getStockActual()
        );
        alertaStockServiceImpl.verificarYGenerarAlerta(producto);
        producto.actualizarStockInicial(stock.getSigno());
        movStockRepository.save(movimientoStock);
        productoRepository.save(movimientoStock.getProducto());
        return movimientoStock;
    }


    // metodo solo para uso del propio sistema
    @Transactional
    public List<MovimientoStock> MoverStockSistema(List<MovimientoStock> movStock){
        List<MovimientoStock> listaSinStock = new ArrayList<>();
        for (MovimientoStock stock : movStock){
            Boolean ok = stock.getProducto().moverStock(stock.getCantidad(), stock.getSigno());
            if(ok){
                // Obtener la fecha y hora del sistema
                LocalDateTime ahora = LocalDateTime.now();
                // Definir la zona horaria de Uruguay
                ZoneId zona = ZoneId.of("America/Montevideo");
                // Obtener la fecha y hora actual en la zona de Uruguay
                ZonedDateTime fechaConZona = ahora.atZone(zona);
                stock.setFecha(fechaConZona.toLocalDateTime());
                stock.setProductoStock(stock.getProducto().getStockActual());
                movStockRepository.save(stock);
                productoRepository.save(stock.getProducto());

            }else{
                listaSinStock.add(stock);
            }
        }
    return listaSinStock;
    }

    public void PuedoMovStockSistema(List<MovimientoStock> movStock) throws StockException {
        for(MovimientoStock stock : movStock){
            Producto producto = productoRepository.findById(stock.getProducto().getIdProducto()).orElse(null);
            if (producto == null) throw new StockException(" Producto no encontrado ", stock.getProducto());
            Boolean ok = producto.puedoMoverStock(stock.getCantidad(), stock.getSigno());
            if(!ok)
                throw new StockException("No hay stock suficiente del producto : ", stock.getProducto() );
        }
    }

    public List<MovimientoStock> todos(){
        return movStockRepository.findAll();
    }

    public MovimientoStock getById(long id) {
       return  movStockRepository.findById(id).orElse(null);
    }

    public void borrar(long id) throws BadRequestException{
        MovimientoStock m = movStockRepository.findById(id).orElse(null);
       if(m==null)
           throw new BadRequestException("No existe id");
       else
           movStockRepository.deleteById(id);
    }

    public MovimientoStock editar(long id , StockDTO movActualizadoDTO) throws BadRequestException{
        MovimientoStock mov = movStockRepository.findById(id).orElse(null);
       if(mov==null)
           throw new BadRequestException("No existe id stock");

       long idProducto = movActualizadoDTO.getIdProducto();
       Producto producto = productoRepository.findById(idProducto).orElse(null);
       if(producto == null)
           throw new BadRequestException("No existe producto");

       MovimientoStock movActualizado = new MovimientoStock(producto, movActualizadoDTO.getCantidad(),movActualizadoDTO.getMotivo(),movActualizadoDTO.getSigno());

        if (mov.actualizar(movActualizado)) {
            productoRepository.save(producto);
            productoRepository.save(mov.getProducto());
            mov.setProducto(movActualizado.getProducto());
            mov.setCantidad(movActualizado.getCantidad());
            mov.setSigno(movActualizado.getSigno());
            mov.setProductoStock(mov.getProducto().getStockActual());
            movStockRepository.save(mov);
            return mov;
        }
        else throw new BadRequestException("El stock es insuficiente para realizar esta operacion");
    }

    public List<MovimientoStock> decendente(){
        return movStockRepository.findAllOrderByFechaDesc();
    }


}
