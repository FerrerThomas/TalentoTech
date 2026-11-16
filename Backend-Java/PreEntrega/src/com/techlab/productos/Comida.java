package com.techlab.productos;

/**
 * Representa un producto alimenticio sólido dentro del catálogo de TechLab.
 * Demuestra el uso de Herencia y Polimorfismo.
 */
public class Comida extends Producto {
    private double pesoGramos;
    private String fechaVencimiento;

    public Comida(String nombre, double precio, int stock, double pesoGramos, String fechaVencimiento) {
        super(nombre, precio, stock);
        setPesoGramos(pesoGramos);
        setFechaVencimiento(fechaVencimiento);
    }

    public Comida(int id, String nombre, double precio, int stock, double pesoGramos, String fechaVencimiento) {
        super(id, nombre, precio, stock);
        setPesoGramos(pesoGramos);
        setFechaVencimiento(fechaVencimiento);
    }

    public double getPesoGramos() {
        return pesoGramos;
    }

    public void setPesoGramos(double pesoGramos) {
        if (pesoGramos > 0) {
            this.pesoGramos = pesoGramos;
        } else {
            throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        }
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        if (fechaVencimiento != null && !fechaVencimiento.trim().isEmpty()) {
            this.fechaVencimiento = fechaVencimiento.trim();
        } else {
            this.fechaVencimiento = "N/D";
        }
    }

    @Override
    public String getTipo() {
        return "Comida";
    }

    @Override
    public String mostrarDetalles() {
        return String.format("Peso: %.1fg, Vencimiento: %s", pesoGramos, fechaVencimiento);
    }
}
