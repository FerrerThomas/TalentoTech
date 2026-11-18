package com.techlab.excepciones;

public class StockInsuficienteException extends RuntimeException {
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
