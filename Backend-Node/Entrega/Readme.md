# Entrega Final - TalenTech Backend Node

API REST para consultar, crear y eliminar productos, construida con Express, protegida con JSON Web Tokens (JWT) e integrada con Firebase Firestore.

**Entrega:** https://backend-node-tomasferrer.vercel.app

## Requisitos

- Node.js instalado.
- Credenciales de Firebase configuradas en un archivo `.env` en la raíz del proyecto.

## Cómo ejecutarlo

1. Abre una terminal en la carpeta del proyecto.
2. Instala las dependencias:
   ```bash
   npm install
   ```
3. Ejecuta el servidor:
   ```bash
   npm start
   ```
4. El servidor estará disponible en `http://localhost:3000`.

---

## Documentación Interactiva (Swagger)

La forma más fácil de probar y visualizar los endpoints es mediante Swagger UI.
Con el servidor corriendo, abre tu navegador y entra a:
👉 `http://localhost:3000/api-docs`

---

## Cómo usar los endpoints manualmente (Postman / Thunder Client)

Esta API está protegida. Para acceder a los recursos de productos, primero debes generar un token de acceso.

### 1. Iniciar Sesión (Login)

**Ruta:** `POST /auth/login`

Debes enviar las credenciales en formato JSON en el *Body* de la petición:
```json
{
  "email": "user@email.com",
  "password": "strongPass123"
}
```
**Respuesta:** Obtendrás el token de autorización.
```json
{
  "token": "eyJh..."
}
```

### 2. Autenticar peticiones

Para usar cualquier ruta de productos, debes incluir el token obtenido en la pestaña **Headers** (o pestaña Authorization > Bearer Token en Swagger):
- **Key:** `Authorization`
- **Value:** `<tu_token_aqui>`

### 3. Endpoints de Productos

Todos estos endpoints **requieren token de autorización**:

- **Ver todos los productos:** 
  `GET /api/products`
  
- **Ver un producto por ID:**
  `GET /api/products/:id`
  
- **Crear un nuevo producto:**
  `POST /api/products/create`
  *Debes enviar un JSON en el Body:*
  ```json
  {
    "title": "Remera de prueba",
    "price": 1500,
    "category": "Ropa"
  }
  ```

- **Eliminar un producto:**
  `DELETE /api/products/:id`
  *(El `:id` es el código alfanumérico que genera Firebase).*
