package com.techlab.pedidos;

import com.techlab.productos.Producto;

/**
 * Representa una línea individual de producto y cantidad.
 * Utilizada como colaboración de objetos y estructura intermedia.
 */
public class LineaPedido {
    private Producto producto;
    private int cantidad;

    public LineaPedido() {
    }

    public LineaPedido(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad > 0) {
            this.cantidad = cantidad;
        }
    }

    /**
     * Indica si el producto tiene stock suficiente para cubrir la cantidad solicitada.
     */
    public boolean hayStockSuficiente() {
        return producto.getStock() != null && cantidad <= producto.getStock();
    }

    public double calcularSubtotal() {
        return (producto != null && producto.getPrecio() != null) ? producto.getPrecio() * cantidad : 0.0;
    }

    @Override
    public String toString() {
        return String.format("%-25s x %-3d  | Precio Unit: $%8.2f | Subtotal: $%8.2f",
                producto.getNombre(), cantidad, producto.getPrecio(), calcularSubtotal());
    }
}
