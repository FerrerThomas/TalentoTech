package com.techlab.productos;

import com.techlab.entity.Categoria;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Comida")
public class Comida extends Producto {

    @Column(name = "peso_gramos")
    private Double pesoGramos;

    @Column(name = "fecha_vencimiento", length = 50)
    private String fechaVencimiento;

    public Comida() {
        super();
    }

    public Comida(String nombre, String descripcion, Double precio, Integer stock, String imagenUrl,
                  Categoria categoria, Double pesoGramos, String fechaVencimiento) {
        super(nombre, descripcion, precio, stock, imagenUrl, categoria);
        this.pesoGramos = pesoGramos;
        this.fechaVencimiento = fechaVencimiento;
    }

    public Double getPesoGramos() {
        return pesoGramos;
    }

    public void setPesoGramos(Double pesoGramos) {
        this.pesoGramos = pesoGramos;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public String getTipo() {
        return "Comida";
    }

    @Override
    public String mostrarDetalles() {
        return String.format("Peso: %.1fg, Vto: %s",
                pesoGramos != null ? pesoGramos : 0.0,
                fechaVencimiento != null ? fechaVencimiento : "N/D");
    }
}
