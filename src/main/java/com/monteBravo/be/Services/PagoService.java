package com.monteBravo.be.Services;

import com.monteBravo.be.Repository.PagoRepository;
import com.monteBravo.be.Repository.ProductoRepository;

import com.monteBravo.be.entity.Pago;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagoService {


    private final PagoRepository pagoRepository;



    public PagoService(PagoRepository pagoRepository, ProductoRepository productoRepository) {
        this.pagoRepository = pagoRepository;

    }


    public Optional<Pago> obtenerPagoPorIdMercadoPago(Long idMercadoPago) {
        return pagoRepository.findByIdMercadoPago(idMercadoPago);
    }


    public Pago guardar(Pago pago){
        return pagoRepository.save(pago);

    }


}

