package com.techlab.servicio;

import com.techlab.dto.ItemPedidoDTO;
import com.techlab.dto.PedidoRequestDTO;
import com.techlab.entity.EstadoPedido;
import com.techlab.entity.Usuario;
import com.techlab.excepciones.ProductoNoEncontradoException;
import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.DetallePedido;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;
import com.techlab.repository.PedidoRepository;
import com.techlab.repository.ProductoRepository;
import com.techlab.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró el pedido #" + id));
    }

    /**
     * Procesa y registra un pedido:
     * 1. Verifica la existencia del usuario.
     * 2. Valida preventivamente el stock disponible para TODOS los ítems.
     * 3. Si algún ítem no tiene stock suficiente, lanza StockInsuficienteException (provocando HTTP 400).
     * 4. Si todos son válidos, descuenta el stock de manera atómica, genera los detalles y guarda el pedido.
     */
    @Transactional
    public Pedido procesarPedido(PedidoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ProductoNoEncontradoException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        if (request.getItemsPedido() == null || request.getItemsPedido().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto.");
        }

        // Paso 1: Armar las líneas del pedido (producto + cantidad) y validar stock de TODAS
        List<LineaPedido> lineas = new ArrayList<>();
        for (ItemPedidoDTO item : request.getItemsPedido()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + item.getProductoId()));

            LineaPedido linea = new LineaPedido(producto, item.getCantidad());
            if (!linea.hayStockSuficiente()) {
                throw new StockInsuficienteException(
                        producto.getNombre(),
                        producto.getStock(),
                        linea.getCantidad()
                );
            }
            lineas.add(linea);
        }

        // Paso 2: Creación del pedido y descuento de stock (solo si todas las líneas son válidas)
        Pedido nuevoPedido = new Pedido(usuario);

        for (LineaPedido linea : lineas) {
            Producto producto = linea.getProducto();

            // Descontar stock
            producto.descontarStock(linea.getCantidad());
            productoRepository.save(producto);

            // Persistir la línea como detalle del pedido (con precio unitario y subtotal)
            DetallePedido detalle = new DetallePedido(nuevoPedido, producto, linea.getCantidad());
            nuevoPedido.agregarDetalle(detalle);
        }

        return pedidoRepository.save(nuevoPedido);
    }

    /**
     * Actualiza el estado de un pedido (PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO).
     * Si se cancela un pedido previamente no entregado, restituye el stock al inventario.
     */
    @Transactional
    public Pedido actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPorId(id);
        EstadoPedido estadoAnterior = pedido.getEstado();

        if (nuevoEstado == EstadoPedido.CANCELADO && estadoAnterior != EstadoPedido.CANCELADO) {
            // Reponer stock si se cancela
            for (DetallePedido detalle : pedido.getItems()) {
                Producto prod = detalle.getProducto();
                prod.reponerStock(detalle.getCantidad());
                productoRepository.save(prod);
            }
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
}
