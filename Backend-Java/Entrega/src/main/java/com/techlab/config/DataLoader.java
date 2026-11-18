package com.techlab.config;

import com.techlab.entity.Categoria;
import com.techlab.entity.EstadoPedido;
import com.techlab.entity.Usuario;
import com.techlab.pedidos.DetallePedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Bebida;
import com.techlab.productos.Comida;
import com.techlab.productos.Producto;
import com.techlab.repository.CategoriaRepository;
import com.techlab.repository.PedidoRepository;
import com.techlab.repository.ProductoRepository;
import com.techlab.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Precarga de datos representativos del e-commerce gourmet Sibelius.
 * Facilita la corrección y prueba inmediata del frontend y backend sin carga manual.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    public DataLoader(CategoriaRepository categoriaRepository,
                      ProductoRepository productoRepository,
                      UsuarioRepository usuarioRepository,
                      PedidoRepository pedidoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() > 0) {
            return; // Ya existen datos
        }

        // 1. Categorías
        Categoria catCafe = categoriaRepository.save(new Categoria("Café de Especialidad", "Granos seleccionados y tostados de origen único"));
        Categoria catTe = categoriaRepository.save(new Categoria("Té en Hebras", "Variedades orgánicas y blends aromáticos"));
        Categoria catBakery = categoriaRepository.save(new Categoria("Pastelería Artesanal", "Elaboración diaria con ingredientes naturales"));
        Categoria catAccesorios = categoriaRepository.save(new Categoria("Accesorios Barista", "Métodos de filtrado, tazas y equipamiento"));

        // 2. Usuarios
        Usuario admin = usuarioRepository.save(new Usuario("Silvia (Product Owner)", "silvia@techlab.com", "ADMIN"));
        Usuario cliente1 = usuarioRepository.save(new Usuario("Matías Desarrollador", "matias@techlab.com", "CLIENTE"));
        Usuario cliente2 = usuarioRepository.save(new Usuario("Sabrina Desarrolladora", "sabrina@techlab.com", "CLIENTE"));
        Usuario cliente3 = usuarioRepository.save(new Usuario("Luis Diseñador UX", "luis@techlab.com", "CLIENTE"));
        // Usuario 5: Cliente Gourmet Demo (coincide con los ejemplos del enunciado "usuarioId: 5")
        Usuario clienteDemo = usuarioRepository.save(new Usuario("Cliente Gourmet Demo", "cliente.demo@sibelius.com", "CLIENTE"));

        // 3. Productos (Bebidas, Comidas y Generales con imágenes reales de alta calidad)
        Producto cafePremium = productoRepository.save(new Bebida(
                "Café Premium Colombiano",
                "Café colombiano tostado medio con notas achocolatadas y florales",
                12.50,
                100,
                "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=600&auto=format&fit=crop&q=80",
                catCafe,
                250,
                false
        ));

        Producto cafeBourbon = productoRepository.save(new Bebida(
                "Café Bourbon Amarillo",
                "Café de altura brasileño con acidez cítrica brillante y cuerpo sedoso",
                15.00,
                45,
                "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80",
                catCafe,
                250,
                false
        ));

        Producto coldBrew = productoRepository.save(new Bebida(
                "Cold Brew Vainilla & Caramelo",
                "Maceración en frío de 18 horas servido con un toque de vainilla",
                14.20,
                25,
                "https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=600&auto=format&fit=crop&q=80",
                catCafe,
                450,
                true
        ));

        Producto teChai = productoRepository.save(new Bebida(
                "Té Chai Especiado Masala",
                "Té negro premium con canela, cardamomo, clavo y jengibre fresco",
                10.50,
                50,
                "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600&auto=format&fit=crop&q=80",
                catTe,
                300,
                false
        ));

        Producto teSencha = productoRepository.save(new Bebida(
                "Té Verde Sencha Orgánico",
                "Cosecha japonesa tradicional de hebras puras rica en antioxidantes",
                11.80,
                30,
                "https://images.unsplash.com/photo-1556881286-fc6915169721?w=600&auto=format&fit=crop&q=80",
                catTe,
                350,
                false
        ));

        Producto croissant = productoRepository.save(new Comida(
                "Croissant Francés de Almendras",
                "Masa hojaldrada con manteca pura, crema frangipane y almendras tostadas",
                8.50,
                20,
                "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=600&auto=format&fit=crop&q=80",
                catBakery,
                140.0,
                "2026-11-25"
        ));

        Producto muffin = productoRepository.save(new Comida(
                "Muffin Húmedo de Arándanos",
                "Bizcocho esponjoso relleno de arándanos silvestres y crumble crujiente",
                7.20,
                4, // Stock crítico para probar alerta de stock mínimo
                "https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=600&auto=format&fit=crop&q=80",
                catBakery,
                160.0,
                "2026-11-20"
        ));

        Producto prensa = productoRepository.save(new Producto(
                "Prensa Francesa de Acero y Vidrio",
                "Cafetera de émbolo de 600ml de vidrio borosilicato resistente al calor",
                34.00,
                3, // Stock crítico para probar alerta de stock mínimo
                "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=600&auto=format&fit=crop&q=80",
                catAccesorios
        ));

        // Productos adicionales: el catálogo arranca con 17 productos, de modo que los IDs
        // usados en los ejemplos del enunciado (productoId 10 y 15) existen desde el inicio.
        productoRepository.save(new Bebida(
                "Espresso Doble Intenso",
                "Doble shot de blend de la casa con crema densa y final a cacao amargo",
                6.80,
                80,
                "https://images.unsplash.com/photo-1510707577719-ae7c14805e3a?w=600&auto=format&fit=crop&q=80",
                catCafe,
                60,
                false
        ));

        productoRepository.save(new Bebida(
                "Flat White con Leche de Avena",
                "Doble ristretto con microespuma sedosa de avena y latte art",
                9.90,
                60,
                "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=600&auto=format&fit=crop&q=80",
                catCafe,
                240,
                false
        ));

        productoRepository.save(new Bebida(
                "Iced Latte de Caramelo Salado",
                "Espresso sobre hielo con leche fría y salsa de caramelo con flor de sal",
                11.40,
                35,
                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=600&auto=format&fit=crop&q=80",
                catCafe,
                400,
                true
        ));

        productoRepository.save(new Bebida(
                "Chocolate Caliente Belga",
                "Chocolate belga 54% fundido en leche entera, cremoso y especiado",
                9.50,
                40,
                "https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?w=600&auto=format&fit=crop&q=80",
                catCafe,
                300,
                false
        ));

        productoRepository.save(new Bebida(
                "Té Earl Grey Imperial",
                "Té negro de Ceylán perfumado con aceite de bergamota y pétalos de aciano",
                10.20,
                45,
                "https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=600&auto=format&fit=crop&q=80",
                catTe,
                300,
                false
        ));

        productoRepository.save(new Bebida(
                "Rooibos Frutos Rojos",
                "Infusión sudafricana sin cafeína con frutilla, frambuesa e hibisco",
                9.80,
                55,
                "https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600&auto=format&fit=crop&q=80",
                catTe,
                350,
                false
        ));

        productoRepository.save(new Comida(
                "Medialunas de Manteca (x6)",
                "Media docena de medialunas hojaldradas con almíbar, horneadas cada mañana",
                12.00,
                30,
                "https://images.unsplash.com/photo-1530610476181-d83430b64dcd?w=600&auto=format&fit=crop&q=80",
                catBakery,
                360.0,
                "2026-12-02"
        ));

        productoRepository.save(new Comida(
                "Cheesecake de Frutos Rojos",
                "Base de galleta crocante, queso crema suave y coulis de frutos del bosque",
                9.90,
                15,
                "https://images.unsplash.com/photo-1524351199678-941a58a3df50?w=600&auto=format&fit=crop&q=80",
                catBakery,
                180.0,
                "2026-12-05"
        ));

        productoRepository.save(new Comida(
                "Torta Ópera de Café",
                "Capas de bizcocho de almendras, crema de café y ganache de chocolate",
                10.50,
                12,
                "https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=600&auto=format&fit=crop&q=80",
                catBakery,
                170.0,
                "2026-12-04"
        ));

        // 4. Pedido inicial de prueba para el cliente demo
        Pedido pedidoInicial = new Pedido(clienteDemo);
        pedidoInicial.setEstado(EstadoPedido.CONFIRMADO);
        pedidoInicial.agregarDetalle(new DetallePedido(pedidoInicial, cafePremium, 2));
        pedidoInicial.agregarDetalle(new DetallePedido(pedidoInicial, croissant, 1));
        pedidoRepository.save(pedidoInicial);
    }
}
