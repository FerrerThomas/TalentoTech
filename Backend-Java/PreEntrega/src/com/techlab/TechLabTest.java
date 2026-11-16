package com.techlab;

import com.techlab.excepciones.ProductoNoEncontradoException;
import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Bebida;
import com.techlab.productos.Comida;
import com.techlab.productos.Producto;
import com.techlab.servicio.PedidoService;
import com.techlab.servicio.ProductoService;

/**
 * Suite de pruebas automatizadas para verificar los requerimientos técnicos:
 * - POO, Encapsulamiento y Validaciones
 * - Herencia y Polimorfismo
 * - Manejo de Colecciones y Colaboración
 * - Lanzamiento y captura de excepciones personalizadas
 * - Cálculo de costos y actualización de inventario
 */
public class TechLabTest {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   EJECUTANDO PRUEBAS AUTOMATIZADAS - TECHLAB BACKEND JAVA     ");
        System.out.println("===============================================================\n");

        int totalPruebas = 0;
        int exitosas = 0;

        // Prueba 1: Encapsulamiento y validación de precios no negativos
        totalPruebas++;
        try {
            Bebida b = new Bebida("Prueba", 100.0, 10, 200, true);
            b.setPrecio(-50.0);
            System.err.println("[FALLO] Prueba 1: Debería haber lanzado IllegalArgumentException ante precio negativo.");
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Prueba 1: Encapsulamiento valida precios negativos correctamente.");
            exitosas++;
        }

        // Prueba 2: Herencia y Polimorfismo
        totalPruebas++;
        Producto prod1 = new Bebida("Café Flat White", 2200.0, 15, 200, false);
        Producto prod2 = new Comida("Alfajor Artesanal", 1200.0, 20, 80.0, "2026-11-30");

        if (prod1.getTipo().equals("Bebida") && prod1.mostrarDetalles().contains("200ml")
                && prod2.getTipo().equals("Comida") && prod2.mostrarDetalles().contains("80")
                && prod2.mostrarDetalles().contains("2026-11-30")) {
            System.out.println("[OK] Prueba 2: Herencia y polimorfismo en Bebida y Comida funcionan correctamente.");
            exitosas++;
        } else {
            System.err.println("[FALLO] Prueba 2: Polimorfismo retornó valores inesperados.");
        }

        // Prueba 3: Búsqueda y excepción ProductoNoEncontradoException
        totalPruebas++;
        ProductoService ps = new ProductoService();
        try {
            ps.buscarPorId(99999);
            System.err.println("[FALLO] Prueba 3: Debería haber lanzado ProductoNoEncontradoException.");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("[OK] Prueba 3: ProductoNoEncontradoException se lanza ante ID inexistente.");
            exitosas++;
        }

        // Prueba 4: Búsqueda por nombre insensible a mayúsculas
        totalPruebas++;
        try {
            Producto encontrado = ps.buscarPorNombre("chai");
            if (encontrado.getNombre().toLowerCase().contains("chai")) {
                System.out.println("[OK] Prueba 4: Búsqueda flexible por nombre ('chai' -> '" + encontrado.getNombre() + "') correcta.");
                exitosas++;
            } else {
                System.err.println("[FALLO] Prueba 4: Producto encontrado incorrecto.");
            }
        } catch (ProductoNoEncontradoException e) {
            System.err.println("[FALLO] Prueba 4: No encontró producto por nombre existente.");
        }

        // Prueba 5: Creación de Pedido, cálculo total y subtotal por línea
        totalPruebas++;
        Pedido pedido = new Pedido();
        Producto cafe = new Bebida("Café Especial", 1500.0, 10, 250, false);
        Producto muffin = new Comida("Muffin", 800.0, 5, 120.0, "2026-10-15");

        pedido.agregarLinea(new LineaPedido(cafe, 2));   // 2 * 1500 = 3000
        pedido.agregarLinea(new LineaPedido(muffin, 3)); // 3 * 800  = 2400
        // Total esperado: 5400.0

        if (Math.abs(pedido.calcularTotal() - 5400.0) < 0.001) {
            System.out.println("[OK] Prueba 5: Cálculo de totales y subtotales en Pedido y LineaPedido correcto ($5400.00).");
            exitosas++;
        } else {
            System.err.println("[FALLO] Prueba 5: Total calculado incorrecto: " + pedido.calcularTotal());
        }

        // Prueba 6: StockInsuficienteException al intentar pedir más del stock
        totalPruebas++;
        PedidoService pedidoService = new PedidoService();
        Pedido pedidoExcesivo = new Pedido();
        pedidoExcesivo.agregarLinea(new LineaPedido(cafe, 50)); // cafe tiene solo 10 en stock

        try {
            pedidoService.procesarPedido(pedidoExcesivo);
            System.err.println("[FALLO] Prueba 6: Debería haber lanzado StockInsuficienteException.");
        } catch (StockInsuficienteException e) {
            System.out.println("[OK] Prueba 6: StockInsuficienteException capturada correctamente (" + e.getMessage() + ").");
            exitosas++;
        }

        // Prueba 7: Descuento exitoso de stock tras procesar pedido válido
        totalPruebas++;
        int stockInicial = cafe.getStock(); // 10
        Pedido pedidoValido = new Pedido();
        pedidoValido.agregarLinea(new LineaPedido(cafe, 4));

        try {
            pedidoService.procesarPedido(pedidoValido);
            int stockFinal = cafe.getStock(); // 6
            if (stockFinal == stockInicial - 4) {
                System.out.println("[OK] Prueba 7: Descuento atómico de stock correcto tras confirmación (10 -> " + stockFinal + ").");
                exitosas++;
            } else {
                System.err.println("[FALLO] Prueba 7: Stock no disminuyó correctamente.");
            }
        } catch (StockInsuficienteException e) {
            System.err.println("[FALLO] Prueba 7: No debería fallar con stock suficiente.");
        }

        // Resumen
        System.out.println("\n---------------------------------------------------------------");
        System.out.printf(" RESULTADO: %d de %d pruebas completadas exitosamente.%n", exitosas, totalPruebas);
        System.out.println("---------------------------------------------------------------");
    }
}
