package com.techlab.excepciones;

/**
 * Excepción personalizada que se lanza cuando se realiza una búsqueda de producto
 * por ID o por nombre y este no se encuentra en el catálogo.
 */
public class ProductoNoEncontradoException extends Exception {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
