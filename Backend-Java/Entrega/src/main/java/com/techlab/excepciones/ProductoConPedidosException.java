package com.techlab.excepciones;

/**
 * Se lanza al intentar eliminar un producto que ya forma parte de uno o más pedidos.
 * Borrarlo rompería el historial de pedidos, por eso la API responde HTTP 409 Conflict.
 */
public class ProductoConPedidosException extends RuntimeException {

    public ProductoConPedidosException(int productoId) {
        super("No se puede eliminar el producto con ID " + productoId
                + " porque forma parte de pedidos registrados. Podés dejar su stock en 0 para que no se venda más.");
    }
}
