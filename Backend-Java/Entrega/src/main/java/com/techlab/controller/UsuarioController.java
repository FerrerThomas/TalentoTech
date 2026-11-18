package com.techlab.controller;

import com.techlab.entity.Usuario;
import com.techlab.pedidos.Pedido;
import com.techlab.servicio.PedidoService;
import com.techlab.servicio.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, PedidoService pedidoService) {
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario guardado = usuarioService.guardar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /**
     * Endpoint requerido en el flujo del e-commerce: GET /api/usuarios/{id}/pedidos
     */
    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(id));
    }
}
