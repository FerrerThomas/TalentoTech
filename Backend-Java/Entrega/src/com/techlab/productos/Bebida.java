package com.techlab.productos;

/**
 * Representa una Bebida dentro del catálogo de TechLab.
 * Demuestra el uso de Herencia y Polimorfismo.
 */
public class Bebida extends Producto {
    private int volumenMl;
    private boolean esFria;

    public Bebida(String nombre, double precio, int stock, int volumenMl, boolean esFria) {
        super(nombre, precio, stock);
        setVolumenMl(volumenMl);
        this.esFria = esFria;
    }

    public Bebida(int id, String nombre, double precio, int stock, int volumenMl, boolean esFria) {
        super(id, nombre, precio, stock);
        setVolumenMl(volumenMl);
        this.esFria = esFria;
    }

    public int getVolumenMl() {
        return volumenMl;
    }

    public void setVolumenMl(int volumenMl) {
        if (volumenMl > 0) {
            this.volumenMl = volumenMl;
        } else {
            throw new IllegalArgumentException("El volumen debe ser mayor a cero.");
        }
    }

    public boolean isEsFria() {
        return esFria;
    }

    public void setEsFria(boolean esFria) {
        this.esFria = esFria;
    }

    @Override
    public String getTipo() {
        return "Bebida";
    }

    @Override
    public String mostrarDetalles() {
        return String.format("Volumen: %dml (%s)", volumenMl, esFria ? "Bebida Fría" : "Bebida Caliente");
    }
}
