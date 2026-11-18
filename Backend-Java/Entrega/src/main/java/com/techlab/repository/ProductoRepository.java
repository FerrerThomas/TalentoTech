package com.techlab.repository;

import com.techlab.productos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);

    // Consulta para alertas de stock mínimo
    List<Producto> findByStockLessThanEqual(Integer stock);
}
