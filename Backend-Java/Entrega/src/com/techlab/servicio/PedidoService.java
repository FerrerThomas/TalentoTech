package com.techlab.servicio;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicio encargado de la gestión de pedidos de TechLab.
 * Valida stock, efectúa los descuentos correspondientes y registra las órdenes.
 */
public class PedidoService {
    private ArrayList<Pedido> pedidos;

    public PedidoService() {
        this.pedidos = new ArrayList<>();
    }

    /**
     * Confirma y registra un pedido:
     * 1. Valida primero que todos los productos cuenten con stock suficiente.
     * 2. Si hay suficiente stock en todas las líneas, procede a descontar las unidades.
     * 3. Registra el pedido en la colección.
     *
     * @param pedido El pedido a procesar.
     * @throws StockInsuficienteException Si algún producto no cuenta con stock suficiente.
     */
    public void procesarPedido(Pedido pedido) throws StockInsuficienteException {
        if (pedido == null || pedido.getLineas().isEmpty()) {
            throw new IllegalArgumentException("El pedido no contiene ninguna línea de producto.");
        }

        // Paso 1: Validación preventiva de stock para TODAS las líneas
        for (LineaPedido linea : pedido.getLineas()) {
            int stockDisponible = linea.getProducto().getStock();
            int cantidadDeseada = linea.getCantidad();
            if (cantidadDeseada > stockDisponible) {
                throw new StockInsuficienteException(
                        linea.getProducto().getNombre(),
                        stockDisponible,
                        cantidadDeseada
                );
            }
        }

        // Paso 2: Como todas las líneas son válidas, descontamos el stock de forma atómica
        for (LineaPedido linea : pedido.getLineas()) {
            linea.getProducto().descontarStock(linea.getCantidad());
        }

        // Paso 3: Almacenamos el pedido confirmado
        pedidos.add(pedido);
    }

    public List<Pedido> getPedidos() {
        return Collections.unmodifiableList(pedidos);
    }

    /**
     * Muestra la lista histórica de pedidos realizados con sus montos totales.
     */
    public void imprimirPedidos() {
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados en el sistema.");
            return;
        }

        System.out.println("\n===============================================================");
        System.out.println("                 HISTORIAL DE PEDIDOS REALIZADOS               ");
        System.out.println("===============================================================");

        double facturacionTotal = 0.0;
        for (Pedido p : pedidos) {
            p.mostrarDetallePedido();
            facturacionTotal += p.calcularTotal();
            System.out.println();
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf(" TOTAL FACTURADO HISTÓRICO (%d pedidos): $%.2f%n", pedidos.size(), facturacionTotal);
        System.out.println("---------------------------------------------------------------");
    }
}
