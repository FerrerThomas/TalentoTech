package com.techlab.servicio;

import com.techlab.excepciones.ProductoNoEncontradoException;
import com.techlab.productos.Bebida;
import com.techlab.productos.Comida;
import com.techlab.productos.Producto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicio encargado de gestionar las operaciones sobre el catálogo de productos:
 * alta, baja, búsqueda, modificación y listado.
 */
public class ProductoService {
    private ArrayList<Producto> catalogo;

    public ProductoService() {
        this.catalogo = new ArrayList<>();
        precargarProductosIniciales();
    }

    /**
     * Precarga productos de demostración basados en el escenario de Sibelius Gourmet.
     */
    private void precargarProductosIniciales() {
        catalogo.add(new Bebida("Café Espresso Molido", 2500.0, 35, 250, false));
        catalogo.add(new Bebida("Té Chai Especiado", 1800.0, 40, 300, false));
        catalogo.add(new Bebida("Cold Brew Vainilla", 3200.0, 20, 500, true));
        catalogo.add(new Comida("Croissant de Almendras", 2200.0, 25, 120.0, "2026-10-01"));
        catalogo.add(new Comida("Muffin de Arándanos", 1950.0, 30, 150.0, "2026-09-30"));
        catalogo.add(new Comida("Chocolate Amargo 80%", 3000.0, 15, 100.0, "2026-12-31"));
    }

    /**
     * Agrega un nuevo producto al catálogo.
     */
    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("No se puede agregar un producto nulo.");
        }
        catalogo.add(producto);
    }

    /**
     * Retorna una vista inmutable de la lista de productos.
     */
    public List<Producto> getCatalogo() {
        return Collections.unmodifiableList(catalogo);
    }

    /**
     * Busca un producto por su identificador único (ID).
     * @param id ID a buscar.
     * @return El Producto encontrado.
     * @throws ProductoNoEncontradoException Si no existe ningún producto con dicho ID.
     */
    public Producto buscarPorId(int id) throws ProductoNoEncontradoException {
        for (Producto p : catalogo) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new ProductoNoEncontradoException("No se encontró ningún producto con ID: " + id);
    }

    /**
     * Busca un producto por coincidencia de nombre (insensible a mayúsculas y espacios).
     * @param nombre Nombre o fragmento de nombre a buscar.
     * @return El Producto encontrado.
     * @throws ProductoNoEncontradoException Si no se encuentra ninguna coincidencia.
     */
    public Producto buscarPorNombre(String nombre) throws ProductoNoEncontradoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda no puede estar vacío.");
        }

        String termino = nombre.trim().toLowerCase();
        // Coincidencia exacta primero
        for (Producto p : catalogo) {
            if (p.getNombre().trim().equalsIgnoreCase(termino)) {
                return p;
            }
        }

        // Coincidencia parcial si no hubo exacta
        for (Producto p : catalogo) {
            if (p.getNombre().toLowerCase().contains(termino)) {
                return p;
            }
        }

        throw new ProductoNoEncontradoException("No se encontró ningún producto con el nombre: \"" + nombre + "\"");
    }

    /**
     * Búsqueda polivalente: si el término es un número busca por ID, de lo contrario por nombre.
     */
    public Producto buscarPorIdONombre(String criterio) throws ProductoNoEncontradoException {
        if (criterio == null || criterio.trim().isEmpty()) {
            throw new IllegalArgumentException("El criterio de búsqueda no puede estar vacío.");
        }

        String limpio = criterio.trim();
        try {
            int id = Integer.parseInt(limpio);
            return buscarPorId(id);
        } catch (NumberFormatException e) {
            return buscarPorNombre(limpio);
        }
    }

    /**
     * Actualiza el precio de un producto.
     */
    public void actualizarPrecio(int id, double nuevoPrecio) throws ProductoNoEncontradoException {
        Producto p = buscarPorId(id);
        p.setPrecio(nuevoPrecio);
    }

    /**
     * Actualiza el stock de un producto.
     */
    public void actualizarStock(int id, int nuevoStock) throws ProductoNoEncontradoException {
        Producto p = buscarPorId(id);
        p.setStock(nuevoStock);
    }

    /**
     * Elimina un producto del catálogo por su ID.
     * @param id ID del producto a eliminar.
     * @return El producto eliminado.
     * @throws ProductoNoEncontradoException Si el producto no existe.
     */
    public Producto eliminarProducto(int id) throws ProductoNoEncontradoException {
        for (int i = 0; i < catalogo.size(); i++) {
            if (catalogo.get(i).getId() == id) {
                return catalogo.remove(i);
            }
        }
        throw new ProductoNoEncontradoException("No se encontró ningún producto con ID " + id + " para eliminar.");
    }

    /**
     * Muestra en pantalla la lista formateada de todos los productos en catálogo.
     */
    public void imprimirCatalogo() {
        if (catalogo.isEmpty()) {
            System.out.println("No hay productos cargados en el sistema.");
            return;
        }

        System.out.println("=========================================================================================================");
        System.out.printf(" %-4s | %-26s | %-8s | %-10s | %-6s | %-30s%n",
                "ID", "Nombre", "Tipo", "Precio", "Stock", "Detalles Específicos");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Producto p : catalogo) {
            System.out.printf(" %-4d | %-26s | %-8s | $%9.2f | %-6d | %-30s%n",
                    p.getId(),
                    p.getNombre(),
                    p.getTipo(),
                    p.getPrecio(),
                    p.getStock(),
                    p.mostrarDetalles());
        }
        System.out.println("=========================================================================================================");
    }
}
