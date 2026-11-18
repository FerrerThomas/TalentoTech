package com.techlab.servicio;

import com.techlab.dto.ProductoDTO;
import com.techlab.entity.Categoria;
import com.techlab.excepciones.ProductoConPedidosException;
import com.techlab.excepciones.ProductoNoEncontradoException;
import com.techlab.productos.Bebida;
import com.techlab.productos.Comida;
import com.techlab.productos.Producto;
import com.techlab.repository.CategoriaRepository;
import com.techlab.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(int id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró ningún producto con ID: " + id));
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<Producto> obtenerAlertasStock(int stockMinimo) {
        return productoRepository.findByStockLessThanEqual(stockMinimo);
    }

    @Transactional
    public Producto guardar(ProductoDTO dto) {
        Categoria categoria = null;
        if (dto.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(dto.getCategoriaId()).orElse(null);
        }

        Producto producto;
        String tipo = (dto.getTipo() != null) ? dto.getTipo().trim().toLowerCase() : "";

        if ("bebida".equals(tipo)) {
            Integer volumen = (dto.getVolumenMl() != null) ? dto.getVolumenMl() : 250;
            Boolean esFria = (dto.getEsFria() != null) ? dto.getEsFria() : false;
            producto = new Bebida(dto.getNombre(), dto.getDescripcion(), dto.getPrecio(),
                    dto.getStock(), dto.getImagenUrl(), categoria, volumen, esFria);
        } else if ("comida".equals(tipo)) {
            Double peso = (dto.getPesoGramos() != null) ? dto.getPesoGramos() : 100.0;
            String vto = (dto.getFechaVencimiento() != null && !dto.getFechaVencimiento().isBlank())
                    ? dto.getFechaVencimiento() : "N/D";
            producto = new Comida(dto.getNombre(), dto.getDescripcion(), dto.getPrecio(),
                    dto.getStock(), dto.getImagenUrl(), categoria, peso, vto);
        } else {
            producto = new Producto(dto.getNombre(), dto.getDescripcion(), dto.getPrecio(),
                    dto.getStock(), dto.getImagenUrl(), categoria);
        }

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto guardarEntidadDirecta(Producto p) {
        return productoRepository.save(p);
    }

    @Transactional
    public Producto actualizar(int id, ProductoDTO dto) {
        Producto existente = obtenerPorId(id);

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            existente.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            existente.setDescripcion(dto.getDescripcion());
        }
        if (dto.getPrecio() != null) {
            existente.setPrecio(dto.getPrecio());
        }
        if (dto.getStock() != null) {
            existente.setStock(dto.getStock());
        }
        if (dto.getImagenUrl() != null && !dto.getImagenUrl().isBlank()) {
            existente.setImagenUrl(dto.getImagenUrl());
        }
        if (dto.getCategoriaId() != null) {
            Categoria cat = categoriaRepository.findById(dto.getCategoriaId()).orElse(null);
            existente.setCategoria(cat);
        }

        // Si es instancia de Bebida
        if (existente instanceof Bebida bebida) {
            if (dto.getVolumenMl() != null) bebida.setVolumenMl(dto.getVolumenMl());
            if (dto.getEsFria() != null) bebida.setEsFria(dto.getEsFria());
        }
        // Si es instancia de Comida
        if (existente instanceof Comida comida) {
            if (dto.getPesoGramos() != null) comida.setPesoGramos(dto.getPesoGramos());
            if (dto.getFechaVencimiento() != null) comida.setFechaVencimiento(dto.getFechaVencimiento());
        }

        return productoRepository.save(existente);
    }

    @Transactional
    public void reponerStock(int id, int cantidad) {
        Producto p = obtenerPorId(id);
        p.reponerStock(cantidad);
        productoRepository.save(p);
    }

    @Transactional
    public void eliminar(int id) {
        if (!productoRepository.existsById(id)) {
            throw new ProductoNoEncontradoException("No se encontró ningún producto con ID: " + id);
        }
        try {
            productoRepository.deleteById(id);
            // flush() fuerza el DELETE ahora, para que una violación de clave foránea
            // (producto referenciado por un pedido) se capture en este bloque.
            productoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ProductoConPedidosException(id);
        }
    }
}
