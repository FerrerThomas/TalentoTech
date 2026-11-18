package com.techlab.productos;

import com.techlab.entity.Categoria;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Bebida")
public class Bebida extends Producto {

    @Column(name = "volumen_ml")
    private Integer volumenMl;

    @Column(name = "es_fria")
    private Boolean esFria;

    public Bebida() {
        super();
    }

    public Bebida(String nombre, String descripcion, Double precio, Integer stock, String imagenUrl,
                  Categoria categoria, Integer volumenMl, Boolean esFria) {
        super(nombre, descripcion, precio, stock, imagenUrl, categoria);
        this.volumenMl = volumenMl;
        this.esFria = esFria;
    }

    public Integer getVolumenMl() {
        return volumenMl;
    }

    public void setVolumenMl(Integer volumenMl) {
        this.volumenMl = volumenMl;
    }

    public Boolean getEsFria() {
        return esFria;
    }

    public void setEsFria(Boolean esFria) {
        this.esFria = esFria;
    }

    @Override
    public String getTipo() {
        return "Bebida";
    }

    @Override
    public String mostrarDetalles() {
        return String.format("Volumen: %dml (%s)",
                volumenMl != null ? volumenMl : 0,
                (esFria != null && esFria) ? "Fría" : "Caliente");
    }
}
