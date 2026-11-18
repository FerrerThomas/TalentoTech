# E-Commerce - Java Back-End - Talento Tech 
**Proyecto:** Entrega Final  (API RESTful Spring Boot + MySQL + Frontend Web)

---

## 📁 1. Estructura de Paquetes y Módulos

```text
Entrega/
├── pom.xml                                      # Gestión de dependencias Maven (Spring Boot 3, Data JPA, MySQL, H2, Validation)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── techlab/
│   │   │           ├── TechLabApplication.java  # Clase principal Spring Boot
│   │   │           ├── Main.java                # Punto de entrada secundario
│   │   │           ├── config/
│   │   │           │   ├── CorsConfig.java      # Configuración global de CORS 
│   │   │           │   └── DataLoader.java      # Precarga de datos iniciales Sibelius
│   │   │           ├── controller/
│   │   │           │   ├── ProductoController.java  # Endpoints CRUD de productos (/api/productos)
│   │   │           │   ├── CategoriaController.java # Endpoints de categorías (/api/categorias)
│   │   │           │   ├── PedidoController.java    # Endpoints de pedidos y estados (/api/pedidos)
│   │   │           │   ├── UsuarioController.java   # Endpoints de usuarios y pedidos por usuario
│   │   │           │   └── AdminController.java     # Alertas de stock crítico y reposición
│   │   │           ├── dto/
│   │   │           │   ├── ItemPedidoDTO.java       # DTO para items solicitados (productoId, cantidad)
│   │   │           │   ├── PedidoRequestDTO.java    # DTO del pedido { usuarioId, itemsPedido }
│   │   │           │   └── ProductoDTO.java         # DTO de altas y modificaciones
│   │   │           ├── entity/
│   │   │           │   ├── Categoria.java           # Entidad JPA @Entity
│   │   │           │   ├── Usuario.java             # Entidad JPA @Entity (nombre, email, rol)
│   │   │           │   └── EstadoPedido.java        # Enum (PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO)
│   │   │           ├── excepciones/
│   │   │           │   ├── StockInsuficienteException.java    # Excepción cuando cantidad > stock (HTTP 400)
│   │   │           │   ├── ProductoNoEncontradoException.java # Excepción ante recurso inexistente (HTTP 404)
│   │   │           │   ├── ProductoConPedidosException.java   # Producto con pedidos no se puede eliminar (HTTP 409)
│   │   │           │   └── GlobalExceptionHandler.java        # Manejador @RestControllerAdvice
│   │   │           ├── pedidos/
│   │   │           │   ├── Pedido.java              # Entidad JPA con cálculo de total y estado
│   │   │           │   ├── DetallePedido.java       # Entidad JPA: relación Pedido - Producto con subtotal
│   │   │           │   └── LineaPedido.java         # Objeto de colaboración y cálculo intermedio
│   │   │           ├── productos/
│   │   │           │   ├── Producto.java            # Entidad JPA base con encapsulamiento y lógica de stock
│   │   │           │   ├── Bebida.java              # Subclase polimórfica (volumen en ml, fría/caliente)
│   │   │           │   └── Comida.java              # Subclase polimórfica (peso en gramos, vencimiento)
│   │   │           ├── repository/
│   │   │           │   ├── ProductoRepository.java  # Spring Data JPA con queries personalizadas
│   │   │           │   ├── CategoriaRepository.java # Spring Data JPA
│   │   │           │   ├── PedidoRepository.java    # Spring Data JPA (por usuario y fecha)
│   │   │           │   ├── DetallePedidoRepository.java
│   │   │           │   └── UsuarioRepository.java   # Spring Data JPA
│   │   │           └── servicio/
│   │   │               ├── ProductoService.java     # Lógica de catálogo, CRUD y validaciones
│   │   │               ├── PedidoService.java       # Validación atómica de stock y descuento
│   │   │               ├── CategoriaService.java    # Lógica de categorías
│   │   │               └── UsuarioService.java      # Lógica de usuarios
│   │   └── resources/
│   │       ├── application.properties           # Conexión MySQL techlabdb y configuración JPA
│   │       ├── application-h2.properties        # Perfil de prueba autónoma H2 sin requerir MySQL
│   │       └── static/                          # FRONTEND WEB INTEGRADO (Template Adaptado)
│   │           ├── index.html                   # Página web del e-commerce con Menú TechLab
│   │           ├── css/styles.css               # Estilos temáticos de Sibelius Coffee
│   │           └── js/app.js                    # Consumo de la API REST, Carrito y DOM
│   └── test/
│       └── java/
│           └── com/techlab/TechLabApplicationTests.java # pruebas automáticas Spring Boot
└── README.md
```

---

## 📡 2. Endpoints de la API REST

### Productos (`/api/productos`)
* `GET /api/productos`: Lista todo el catálogo de productos con stock y categoría.
* `GET /api/productos/{id}`: Detalle de un producto por ID.
* `GET /api/productos/categoria/{categoriaId}`: Filtrar por categoría.
* `GET /api/productos/buscar?nombre=...`: Buscar productos por nombre.
* `POST /api/productos`: Crear nuevo producto (recibe JSON con nombre, descripción, precio, categoríaId, imagenUrl, stock, etc.).
* `PUT /api/productos/{id}`: Actualizar datos de un producto.
* `DELETE /api/productos/{id}`: Eliminar un producto (HTTP 409 si ya forma parte de algún pedido).

### Pedidos (`/api/pedidos`)
* `POST /api/pedidos`: Crear y confirmar pedido:
  ```json
  {
    "usuarioId": 5,
    "itemsPedido": [
      { "productoId": 1, "cantidad": 2 },
      { "productoId": 6, "cantidad": 1 }
    ]
  }
  ```
  * *Si hay stock:* Retorna HTTP 201 Created con el pedido generado en estado `PENDIENTE` y descuenta el stock.
  * *Si no hay stock:* Retorna HTTP 400 Bad Request con mensaje de `StockInsuficienteException`.
* `GET /api/pedidos`: Lista histórico general de órdenes.
* `GET /api/usuarios/{usuarioId}/pedidos`: Historial de compras filtrado por usuario.
* `PUT /api/pedidos/{id}/estado?nuevoEstado=CONFIRMADO`: Actualiza el estado (`PENDIENTE`, `CONFIRMADO`, `ENVIADO`, `ENTREGADO`, `CANCELADO`).

### Administración (`/api/admin`)
* `GET /api/admin/alertas-stock?minimo=5`: Lista productos cuyo stock es menor o igual al umbral crítico.
* `POST /api/admin/reponer-stock/{productoId}?cantidad=10`: Reabastece el inventario de un producto.
* `GET /api/admin/resumen`: Indicadores clave (total productos, total pedidos, stock crítico, usuarios).

---

## 🚀 3. Cómo Ejecutar el Proyecto

### Opción 1: Desde IntelliJ IDEA o Eclipse
1. Abrir la carpeta `Entrega` como proyecto Maven existente.
2. Si utilizas **MySQL**:
   * Asegúrate de tener el servidor MySQL encendido.
   * En MySQL Workbench ejecuta:
     ```sql
     CREATE DATABASE techlabdb;
     USE techlabdb;
     ```
   * En `src/main/resources/application.properties`, revisa tu usuario y contraseña de MySQL (por defecto `root` / `root`).
3. Si deseas probar **sin instalar MySQL (Base H2 en memoria)**, elige una de estas opciones:
   * En `application.properties`, descomenta la línea:
     ```properties
     spring.profiles.active=h2
     ```
   * O, sin modificar archivos, ejecuta desde la consola:
     ```bash
     mvn spring-boot:run -Dspring-boot.run.profiles=h2
     # o bien, con el .jar ya compilado:
     java -jar target/ecommerce-backend-1.0.0.jar --spring.profiles.active=h2
     ```
   * Consola de la base H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:techlabdb`, usuario `sa`, sin contraseña).
4. Navega a `src/main/java/com/techlab/TechLabApplication.java` (o `Main.java`), haz clic derecho y selecciona **Run 'TechLabApplication.main()'**.
5. Abre tu navegador web en:
   👉 **`http://localhost:8080/`**

---

### Opción 2: Ejecutar las Pruebas Automatizadas
Haz clic derecho sobre `src/test/java/com/techlab/TechLabApplicationTests.java` y selecciona **Run 'TechLabApplicationTests'**. Validará automáticamente:
1. Carga del contexto Spring Boot y precarga de catálogo `DataLoader`.
2. Persistencia polimórfica de `Bebida` y `Comida`.
3. Creación de pedidos válidos y descuento atómico de stock.
4. Disparo y captura de `StockInsuficienteException` ante compras que superen las existencias.
5. Reporte de alertas de inventario crítico.
