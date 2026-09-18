package com.techlab.pedidos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una Orden o Pedido de compra en TechLab.
 * Contiene múltiples líneas de productos asociados y calcula el costo total.
 */
public class Pedido {
    private static int contadorPedidos = 0;

    private int id;
    private ArrayList<LineaPedido> lineas;
    private LocalDateTime fechaHora;

    public Pedido() {
        contadorPedidos++;
        this.id = contadorPedidos;
        this.lineas = new ArrayList<>();
        this.fechaHora = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fechaHora.format(formatter);
    }

    public List<LineaPedido> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    /**
     * Agrega una línea de pedido. Si el producto ya existe en otra línea, acumula la cantidad.
     */
    public void agregarLinea(LineaPedido nuevaLinea) {
        if (nuevaLinea == null) {
            return;
        }

        boolean productoExistente = false;
        for (LineaPedido l : lineas) {
            if (l.getProducto().getId() == nuevaLinea.getProducto().getId()) {
                l.setCantidad(l.getCantidad() + nuevaLinea.getCantidad());
                productoExistente = true;
                break;
            }
        }

        if (!productoExistente) {
            lineas.add(nuevaLinea);
        }
    }

    /**
     * Calcula el costo total sumando los subtotales de cada línea del pedido.
     * @return Total a abonar.
     */
    public double calcularTotal() {
        double total = 0.0;
        for (LineaPedido linea : lineas) {
            total += linea.calcularSubtotal();
        }
        return total;
    }

    /**
     * Imprime un desglose completo del pedido con cada producto, cantidad y total.
     */
    public void mostrarDetallePedido() {
        System.out.println("===============================================================");
        System.out.printf(" PEDIDO #%04d | Fecha: %s%n", id, getFechaFormateada());
        System.out.println("===============================================================");
        System.out.printf(" %-4s | %-25s | %-8s | %-10s | %-10s%n",
                "Item", "Producto", "Cantidad", "Precio Unit", "Subtotal");
        System.out.println("---------------------------------------------------------------");

        int item = 1;
        for (LineaPedido linea : lineas) {
            System.out.printf(" %-4d | %-25s | %-8d | $%9.2f | $%9.2f%n",
                    item++,
                    linea.getProducto().getNombre(),
                    linea.getCantidad(),
                    linea.getProducto().getPrecio(),
                    linea.calcularSubtotal());
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf(" COSTO TOTAL DEL PEDIDO: $%.2f%n", calcularTotal());
        System.out.println("===============================================================");
    }
}
