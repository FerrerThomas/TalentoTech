package com.techlab.productos;

import com.techlab.excepciones.StockInsuficienteException;

/**
 * Clase abstracta / base Producto.
 * Representa un artículo comercializable en TechLab.
 * Aplica principios de Encapsulamiento, Métodos de Instancia y Variables Estáticas.
 */
public abstract class Producto {
    // Variable estática para llevar la cuenta total de productos creados
    private static int contadorProductos = 0;

    // Atributos privados (Encapsulamiento)
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    /**
     * Constructor con asignación automática de ID autoincremental.
     * @param nombre Nombre del producto.
     * @param precio Precio unitario (> 0).
     * @param stock Cantidad en stock (>= 0).
     */
    public Producto(String nombre, double precio, int stock) {
        contadorProductos++;
        this.id = contadorProductos;
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
    }

    /**
     * Constructor con ID explícito (útil para restauraciones o IDs definidos).
     */
    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        if (id > contadorProductos) {
            contadorProductos = id;
        }
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
    }

    // --- Getters y Setters con validaciones defensivas ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id > 0) {
            this.id = id;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            this.nombre = nombre.trim();
        } else {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio >= 0) {
            this.precio = precio;
        } else {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        } else {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }

    // --- Métodos de Clase (Estáticos) ---

    public static int getContadorProductos() {
        return contadorProductos;
    }

    // --- Métodos de Negocio e Instancia ---

    /**
     * Descuenta stock si la cantidad disponible es suficiente.
     * @param cantidad Cantidad a descontar.
     * @throws StockInsuficienteException si la cantidad supera el stock actual.
     */
    public void descontarStock(int cantidad) throws StockInsuficienteException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a 0.");
        }
        if (cantidad > this.stock) {
            throw new StockInsuficienteException(this.nombre, this.stock, cantidad);
        }
        this.stock -= cantidad;
    }

    /**
     * Repone unidades al stock del producto.
     * @param cantidad Cantidad de unidades a sumar.
     */
    public void reponerStock(int cantidad) {
        if (cantidad > 0) {
            this.stock += cantidad;
        } else {
            throw new IllegalArgumentException("La cantidad a reponer debe ser mayor a 0.");
        }
    }

    /**
     * Método polimórfico que retorna el tipo o categoría de producto.
     */
    public abstract String getTipo();

    /**
     * Método polimórfico que entrega una descripción detallada según la subclase.
     */
    public abstract String mostrarDetalles();

    /**
     * Muestra la información básica por consola.
     */
    public void mostrarInformacion() {
        System.out.printf("[%d] %s (%s) | Precio: $%.2f | Stock: %d | %s%n",
                id, nombre, getTipo(), precio, stock, mostrarDetalles());
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | %-25s | Tipo: %-8s | Precio: $%8.2f | Stock: %-4d | %s",
                id, nombre, getTipo(), precio, stock, mostrarDetalles());
    }
}
