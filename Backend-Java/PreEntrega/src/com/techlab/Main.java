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

import java.util.Scanner;

/**
 * Clase Principal (Main)
 * Orquesta el menú principal interactivo de la aplicación de gestión de TechLab.
 * Maneja entradas de usuario, validaciones, flujos de control y captura de excepciones.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductoService productoService = new ProductoService();
    private static final PedidoService pedidoService = new PedidoService();

    public static void main(String[] args) {
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenu();
            int opcion = leerEntero("Elija una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> menuAgregarProducto();
                case 2 -> menuListarProductos();
                case 3 -> menuBuscarOActualizarProducto();
                case 4 -> menuEliminarProducto();
                case 5 -> menuCrearPedido();
                case 6 -> menuListarPedidos();
                case 7 -> {
                    System.out.println("===============================================================");
                    System.out.println(" ¡Gracias por utilizar el Sistema de Gestión de TechLab!");
                    System.out.println(" Cerrando aplicación...");
                    System.out.println("===============================================================");
                    ejecutando = false;
                }
                default -> System.out.println("Opción inválida. Por favor, ingrese un número del 1 al 7.");
            }

            if (ejecutando) {
                pausarConsola();
            }
        }

        scanner.close();
    }

    /**
     * Despliega en pantalla el encabezado y menú principal tal como fue diseñado.
     */
    private static void mostrarMenu() {
        System.out.println("\n====================================");
        System.out.println("SISTEMA DE GESTIÓN - TECHLAB");
        System.out.println("====================================\n");
        System.out.println(" 1) Agregar producto");
        System.out.println(" 2) Listar productos");
        System.out.println(" 3) Buscar/Actualizar producto");
        System.out.println(" 4) Eliminar producto");
        System.out.println(" 5) Crear un pedido");
        System.out.println(" 6) Listar pedidos");
        System.out.println(" 7) Salir\n");
    }

    // =========================================================================
    // 1) AGREGAR PRODUCTO
    // =========================================================================
    private static void menuAgregarProducto() {
        System.out.println("--- [1] AGREGAR NUEVO PRODUCTO ---");
        System.out.println("Seleccione el tipo de producto a registrar:");
        System.out.println(" 1) Bebida (ej. Café, Té, Cold Brew)");
        System.out.println(" 2) Comida (ej. Croissant, Muffin, Pastelería)");
        int tipo = leerEntero("Tipo de producto (1-2): ");

        if (tipo != 1 && tipo != 2) {
            System.out.println("Tipo inválido. Operación cancelada.");
            return;
        }

        String nombre = leerTextoNoVacio("Nombre del producto: ");
        double precio = leerDoublePositivo("Precio unitario ($): ");
        int stock = leerEnteroNoNegativo("Stock inicial disponible: ");

        Producto nuevoProducto = null;

        if (tipo == 1) {
            int volumen = leerEnteroMayorACero("Volumen en mililitros (ml): ");
            boolean esFria = leerConfirmacion("¿Se sirve fría? (S/N): ");
            nuevoProducto = new Bebida(nombre, precio, stock, volumen, esFria);
        } else {
            double peso = leerDoublePositivo("Peso en gramos: ");
            System.out.print("Fecha de vencimiento (ej. AAAA-MM-DD o enter para N/D): ");
            String fechaVencimiento = scanner.nextLine().trim();
            if (fechaVencimiento.isEmpty()) {
                fechaVencimiento = "N/D";
            }
            nuevoProducto = new Comida(nombre, precio, stock, peso, fechaVencimiento);
        }

        productoService.agregarProducto(nuevoProducto);
        System.out.println("\n>> ¡Producto registrado exitosamente!");
        nuevoProducto.mostrarInformacion();
    }

    // =========================================================================
    // 2) LISTAR PRODUCTOS
    // =========================================================================
    private static void menuListarProductos() {
        System.out.println("--- [2] CATÁLOGO DE PRODUCTOS REGISTRADOS ---");
        productoService.imprimirCatalogo();
        System.out.printf("Total histórico de productos creados: %d%n", Producto.getContadorProductos());
    }

    // =========================================================================
    // 3) BUSCAR / ACTUALIZAR PRODUCTO
    // =========================================================================
    private static void menuBuscarOActualizarProducto() {
        System.out.println("--- [3] BUSCAR Y ACTUALIZAR PRODUCTO ---");
        System.out.print("Ingrese el ID o nombre del producto: ");
        String criterio = scanner.nextLine().trim();

        if (criterio.isEmpty()) {
            System.out.println("El criterio de búsqueda no puede estar vacío.");
            return;
        }

        try {
            Producto producto = productoService.buscarPorIdONombre(criterio);
            System.out.println("\n>> Producto encontrado:");
            System.out.println("---------------------------------------------------------------");
            producto.mostrarInformacion();
            System.out.println("---------------------------------------------------------------");

            System.out.println("\n¿Desea actualizar datos de este producto?");
            System.out.println(" 1) Actualizar Precio");
            System.out.println(" 2) Actualizar Stock");
            System.out.println(" 3) Actualizar Ambos (Precio y Stock)");
            System.out.println(" 4) Volver al menú principal");
            int opcionAct = leerEntero("Seleccione una opción: ");

            switch (opcionAct) {
                case 1 -> {
                    double nuevoPrecio = leerDoublePositivo("Nuevo precio ($): ");
                    producto.setPrecio(nuevoPrecio);
                    System.out.println(">> Precio actualizado correctamente a $" + nuevoPrecio);
                }
                case 2 -> {
                    int nuevoStock = leerEnteroNoNegativo("Nuevo stock disponible: ");
                    producto.setStock(nuevoStock);
                    System.out.println(">> Stock actualizado correctamente a " + nuevoStock + " unidades.");
                }
                case 3 -> {
                    double nuevoPrecio = leerDoublePositivo("Nuevo precio ($): ");
                    int nuevoStock = leerEnteroNoNegativo("Nuevo stock disponible: ");
                    producto.setPrecio(nuevoPrecio);
                    producto.setStock(nuevoStock);
                    System.out.println(">> Precio y Stock actualizados correctamente.");
                }
                case 4 -> System.out.println("Volviendo al menú principal sin modificaciones.");
                default -> System.out.println("Opción no reconocida. Sin cambios.");
            }

        } catch (ProductoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4) ELIMINAR PRODUCTO
    // =========================================================================
    private static void menuEliminarProducto() {
        System.out.println("--- [4] ELIMINAR PRODUCTO ---");
        int id = leerEntero("Ingrese el ID del producto que desea eliminar: ");

        try {
            Producto p = productoService.buscarPorId(id);
            System.out.println("\nProducto seleccionado para eliminar:");
            p.mostrarInformacion();

            boolean confirmar = leerConfirmacion("¿Está seguro de que desea eliminar este producto? (S/N): ");
            if (confirmar) {
                productoService.eliminarProducto(id);
                System.out.printf(">> El producto '%s' (ID: %d) ha sido eliminado exitosamente.%n", p.getNombre(), id);
            } else {
                System.out.println("Eliminación cancelada.");
            }
        } catch (ProductoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5) CREAR UN PEDIDO
    // =========================================================================
    private static void menuCrearPedido() {
        System.out.println("--- [5] CREAR UN NUEVO PEDIDO ---");
        System.out.println("A continuación se muestra el catálogo actual para su referencia:");
        productoService.imprimirCatalogo();

        Pedido nuevoPedido = new Pedido();
        boolean agregandoProductos = true;

        while (agregandoProductos) {
            System.out.println("\n--- Agregar ítem al pedido ---");
            int idProducto = leerEntero("Ingrese el ID del producto (o 0 para terminar de agregar): ");

            if (idProducto == 0) {
                agregandoProductos = false;
                break;
            }

            try {
                Producto producto = productoService.buscarPorId(idProducto);

                if (producto.getStock() == 0) {
                    System.out.printf("¡Atención! El producto '%s' no cuenta con stock disponible actualmente.%n",
                            producto.getNombre());
                    continue;
                }

                System.out.printf("Producto seleccionado: %s | Precio: $%.2f | Stock disponible: %d%n",
                        producto.getNombre(), producto.getPrecio(), producto.getStock());

                int cantidad = leerEnteroMayorACero("Ingrese la cantidad deseada: ");

                if (cantidad > producto.getStock()) {
                    System.out.printf("Error: La cantidad solicitada (%d) supera el stock disponible (%d).%n",
                            cantidad, producto.getStock());
                    continue;
                }

                nuevoPedido.agregarLinea(new LineaPedido(producto, cantidad));
                System.out.printf(">> Agregado: %s x %d. Costo acumulado del pedido: $%.2f%n",
                        producto.getNombre(), cantidad, nuevoPedido.calcularTotal());

                boolean agregarMas = leerConfirmacion("¿Desea agregar otro producto al pedido? (S/N): ");
                if (!agregarMas) {
                    agregandoProductos = false;
                }

            } catch (ProductoNoEncontradoException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
            }
        }

        if (nuevoPedido.getLineas().isEmpty()) {
            System.out.println("No se agregaron productos al pedido. Operación cancelada.");
            return;
        }

        // Resumen preliminar antes de confirmar
        System.out.println("\nResumen del pedido a confirmar:");
        nuevoPedido.mostrarDetallePedido();

        boolean confirmarPedido = leerConfirmacion("¿Desea confirmar el pedido y descontar el stock? (S/N): ");
        if (confirmarPedido) {
            try {
                pedidoService.procesarPedido(nuevoPedido);
                System.out.println("\n>> ¡PEDIDO CONFIRMADO CON ÉXITO!");
                System.out.println("El stock ha sido actualizado en el catálogo.");
            } catch (StockInsuficienteException e) {
                System.out.println("\n[ERROR AL PROCESAR PEDIDO]: " + e.getMessage());
                System.out.println("No se pudo confirmar el pedido debido a falta de stock.");
            }
        } else {
            System.out.println("Pedido cancelado. El stock de los productos no ha sido modificado.");
        }
    }

    // =========================================================================
    // 6) LISTAR PEDIDOS
    // =========================================================================
    private static void menuListarPedidos() {
        System.out.println("--- [6] LISTADO DE PEDIDOS REALIZADOS ---");
        pedidoService.imprimirPedidos();
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE ENTRADA Y VALIDACIÓN CON MANEJO DE EXCEPCIONES
    // =========================================================================

    /**
     * Lee un número entero de la consola capturando NumberFormatException.
     */
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero válido.");
            }
        }
    }

    /**
     * Lee un entero estrictamente mayor a 0.
     */
    private static int leerEnteroMayorACero(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("Error: El valor debe ser mayor a cero.");
        }
    }

    /**
     * Lee un entero no negativo (>= 0).
     */
    private static int leerEnteroNoNegativo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= 0) {
                return valor;
            }
            System.out.println("Error: El valor no puede ser negativo.");
        }
    }

    /**
     * Lee un valor decimal (double) capturando NumberFormatException y validando > 0.
     */
    private static double leerDoublePositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(input);
                if (valor > 0) {
                    return valor;
                }
                System.out.println("Error: El valor debe ser mayor a cero.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un valor numérico decimal válido (ej. 150.50).");
            }
        }
    }

    /**
     * Lee una cadena de texto asegurando que no esté vacía.
     */
    private static String leerTextoNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: El campo no puede quedar en blanco.");
        }
    }

    /**
     * Solicita confirmación booleana al usuario (S/N).
     */
    private static boolean leerConfirmacion(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String respuesta = scanner.nextLine().trim().toUpperCase();
            if (respuesta.equals("S") || respuesta.equals("SI") || respuesta.equals("SÍ")) {
                return true;
            }
            if (respuesta.equals("N") || respuesta.equals("NO")) {
                return false;
            }
            System.out.println("Responda con 'S' (Sí) o 'N' (No).");
        }
    }

    /**
     * Pausa para permitir la lectura de los resultados antes de re-dibujar el menú.
     */
    private static void pausarConsola() {
        System.out.print("\nPresione Enter para continuar...");
        try {
            scanner.nextLine();
        } catch (Exception ignored) {
        }
    }
}
