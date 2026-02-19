package com.monteBravo.be.Services;

import com.monteBravo.be.Repository.CarritoProductoRepository;
import com.monteBravo.be.Repository.CarritoRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.entity.CarritoProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoProductoService {

    @Autowired
    private CarritoProductoRepository carritoProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoRepository carritoRepository;


    @Transactional
    public void borrarTodosLosCarritoProductos(Long carritoId){

        carritoProductoRepository.deleteAllByCarritoId(carritoId);
    }

    public Optional<CarritoProducto> findByIdProductoAndCarritoId(Long idProducto, Long carritoId){
        return carritoProductoRepository.findCarritoProductoByCarritoIdAndProductoId(carritoId,idProducto);
    }
    public void guardar(CarritoProducto cp){
        carritoProductoRepository.save(cp);
    }

    public List<CarritoProducto> findAll(){
        return carritoProductoRepository.findAll();
    }

    public Optional<CarritoProducto> findById(Long id){

        return carritoProductoRepository.findById(id);
    }

    public void borrar(CarritoProducto cp){
        carritoProductoRepository.delete(cp);
    }

}
