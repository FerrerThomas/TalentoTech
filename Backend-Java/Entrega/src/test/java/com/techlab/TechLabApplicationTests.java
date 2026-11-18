package com.techlab;

import com.techlab.dto.ItemPedidoDTO;
import com.techlab.dto.PedidoRequestDTO;
import com.techlab.entity.EstadoPedido;
import com.techlab.entity.Usuario;
import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Bebida;
import com.techlab.productos.Comida;
import com.techlab.productos.Producto;
import com.techlab.repository.PedidoRepository;
import com.techlab.repository.ProductoRepository;
import com.techlab.repository.UsuarioRepository;
import com.techlab.servicio.PedidoService;
import com.techlab.servicio.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class TechLabApplicationTests {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    @Test
    @DisplayName("1. Contexto Spring Boot y DataLoader cargan productos iniciales")
    void testContextLoadsAndInitialDataLoaded() {
        long totalProductos = productoRepository.count();
        assertTrue(totalProductos >= 6, "Deben existir al menos 6 productos precargados por DataLoader.");
    }

    @Test
    @DisplayName("2. Polimorfismo en base de datos: Bebida y Comida guardan atributos específicos")
    void testPolimorfismoProductos() {
        List<Producto> productos = productoRepository.findAll();
        boolean tieneBebida = productos.stream().anyMatch(p -> p instanceof Bebida && ((Bebida) p).getVolumenMl() != null);
        boolean tieneComida = productos.stream().anyMatch(p -> p instanceof Comida && ((Comida) p).getPesoGramos() != null);

        assertTrue(tieneBebida, "Debe existir al menos un producto polimórfico de tipo Bebida.");
        assertTrue(tieneComida, "Debe existir al menos un producto polimórfico de tipo Comida.");
    }

    @Test
    @Transactional
    @DisplayName("3. Procesamiento de pedido válido: Descuenta stock y genera estado PENDIENTE")
    void testCrearPedidoValidoDescuentaStock() {
        Usuario usuario = usuarioRepository.findAll().get(0);
        Producto producto = productoRepository.findAll().stream()
                .filter(p -> p.getStock() > 10)
                .findFirst()
                .orElseThrow();

        int stockPrevio = producto.getStock();
        int cantidadPedida = 2;

        PedidoRequestDTO request = new PedidoRequestDTO(
                usuario.getId(),
                List.of(new ItemPedidoDTO(producto.getId(), cantidadPedida))
        );

        Pedido pedido = pedidoService.procesarPedido(request);

        assertNotNull(pedido.getId(), "El pedido debe tener un ID asignado por la base de datos.");
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado(), "El estado inicial debe ser PENDIENTE.");
        
        Producto actualizado = productoRepository.findById(producto.getId()).orElseThrow();
        assertEquals(stockPrevio - cantidadPedida, actualizado.getStock(), "El stock debe haberse descontado exactamente.");
    }

    @Test
    @DisplayName("4. Excepción personalizada: Cantidad mayor al stock lanza StockInsuficienteException")
    void testStockInsuficienteException() {
        Usuario usuario = usuarioRepository.findAll().get(0);
        Producto producto = productoRepository.findAll().get(0);

        PedidoRequestDTO requestExcesivo = new PedidoRequestDTO(
                usuario.getId(),
                List.of(new ItemPedidoDTO(producto.getId(), producto.getStock() + 9999))
        );

        assertThrows(StockInsuficienteException.class, () -> {
            pedidoService.procesarPedido(requestExcesivo);
        }, "Debe lanzar StockInsuficienteException ante exceso de cantidad.");
    }

    @Test
    @DisplayName("5. Alertas de stock crítico: Detecta productos con stock menor o igual a 5")
    void testAlertasStockCritico() {
        List<Producto> alertas = productoService.obtenerAlertasStock(5);
        assertNotNull(alertas, "La lista de alertas no debe ser nula.");
        for (Producto p : alertas) {
            assertTrue(p.getStock() <= 5, "Cada producto en alerta debe tener stock menor o igual a 5.");
        }
    }
}
