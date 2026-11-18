package com.techlab.controller;

import com.techlab.entity.Usuario;
import com.techlab.productos.Producto;
import com.techlab.servicio.PedidoService;
import com.techlab.servicio.ProductoService;
import com.techlab.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    @Autowired
    public AdminController(ProductoService productoService,
                           PedidoService pedidoService,
                           UsuarioService usuarioService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Reporte y alertas de stock mínimo (nivel crítico configurable).
     */
    @GetMapping("/alertas-stock")
    public ResponseEntity<List<Producto>> obtenerAlertasStock(
            @RequestParam(defaultValue = "10") int minimo) {
        return ResponseEntity.ok(productoService.obtenerAlertasStock(minimo));
    }

    /**
     * Endpoint administrativo para reponer stock de un producto.
     */
    @PostMapping("/reponer-stock/{productoId}")
    public ResponseEntity<Map<String, Object>> reponerStock(
            @PathVariable int productoId,
            @RequestParam int cantidad) {
        productoService.reponerStock(productoId, cantidad);
        Producto actualizado = productoService.obtenerPorId(productoId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Stock repuesto con éxito");
        respuesta.put("productoId", productoId);
        respuesta.put("nuevoStock", actualizado.getStock());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Métricas generales de administración.
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();
        int totalProductos = productoService.listarTodos().size();
        int totalPedidos = pedidoService.listarTodos().size();
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Producto> alertas = productoService.obtenerAlertasStock(5);

        resumen.put("totalProductos", totalProductos);
        resumen.put("totalPedidos", totalPedidos);
        resumen.put("totalUsuarios", usuarios.size());
        resumen.put("productosStockCritico", alertas.size());
        return ResponseEntity.ok(resumen);
    }
}
