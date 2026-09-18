package com.techlab.excepciones;

/**
 * Excepción personalizada que se lanza cuando se intenta solicitar o vender
 * una cantidad de producto mayor al stock disponible en el inventario.
 */
public class StockInsuficienteException extends Exception {
    private int stockDisponible;
    private int cantidadSolicitada;

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(String nombreProducto, int stockDisponible, int cantidadSolicitada) {
        super(String.format("Stock insuficiente para '%s'. Stock disponible: %d, Cantidad solicitada: %d.",
                nombreProducto, stockDisponible, cantidadSolicitada));
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
