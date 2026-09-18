package com.techlab.pedidos;

import com.techlab.productos.Producto;

/**
 * Representa una línea individual dentro de un Pedido.
 * Modela la colaboración entre clases conteniendo una referencia a Producto y una cantidad.
 */
public class LineaPedido {
    private Producto producto;
    private int cantidad;

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
        if (producto != null) {
            this.producto = producto;
        }
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
     * Calcula el subtotal multiplicando el precio unitario por la cantidad.
     * @return Subtotal en double.
     */
    public double calcularSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    @Override
    public String toString() {
        return String.format("%-25s x %-3d  | Precio Unit: $%8.2f | Subtotal: $%8.2f",
                producto.getNombre(), cantidad, producto.getPrecio(), calcularSubtotal());
    }
}
