package com.monteBravo.be.entity;

import com.monteBravo.be.Inteface.IValidable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "direccion")
public class Direccion implements IValidable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDirecion;
    private String calle;
    private String departamento	;
    private String localidad;
    private String manzana	;
    private String numero;
    private String solar;
    private String apartamento;
    private String descripcion;

    public Direccion(String departamento, String localidad, String manzana, String numero, String solar, String apartamento, String descripcion, String calle) {
        this.departamento = departamento;
        this.localidad = localidad;
        this.manzana = manzana;
        this.numero = numero;
        this.solar = solar;
        this.apartamento = apartamento;
        this.descripcion = descripcion;
        this.calle = calle;
    }

    public Direccion() {

    }

    @Override
    public void validate() throws Exception {
        boolean hasManzanaAndSolar = (manzana != null && !manzana.isEmpty() && solar != null && !solar.isEmpty());
        boolean hasNumero = (numero != null && !numero.isEmpty());

        if (!hasManzanaAndSolar && !hasNumero) {
            throw new Exception("Debe especificar (manzana y solar) o número.");
        }

        if (calle == null || calle.isEmpty()) {
            throw new Exception("El campo 'calle' es obligatorio.");
        }

        if (departamento == null || departamento.isEmpty()) {
            throw new Exception("El campo 'departamento' es obligatorio.");
        }

        if (localidad == null || localidad.isEmpty()) {
            throw new Exception("El campo 'localidad' es obligatorio.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direccion direccion = (Direccion) o;
        return Objects.equals(calle, direccion.calle) && Objects.equals(departamento, direccion.departamento) && Objects.equals(localidad, direccion.localidad) && Objects.equals(manzana, direccion.manzana) && Objects.equals(numero, direccion.numero) && Objects.equals(solar, direccion.solar) && Objects.equals(apartamento, direccion.apartamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, departamento, localidad, manzana, numero, solar, apartamento);
    }
}