/**
 * ===================================================================
 * TECHLAB E-COMMERCE - SIBELIUS COFFEE & SPECIALTY TEA
 * Script JavaScript para interactividad, consumo de API RESTful y Carrito
 * ===================================================================
 */

const API_BASE = (window.location.protocol === 'file:' || (window.location.port !== '8080' && window.location.port !== ''))
  ? 'http://localhost:8080'
  : '';

let productosGlobal = [];
let categoriasGlobal = [];
let usuariosGlobal = [];
let cart = JSON.parse(localStorage.getItem('techlab_cart')) || [];

document.addEventListener('DOMContentLoaded', () => {
  inicializarApp();
});

async function inicializarApp() {
  await cargarCategorias();
  await cargarUsuarios();
  await cargarProductos();
  await cargarPedidos();
  await cargarAdmin();
  updateCartUI();
}

// ===================================================================
// NAVEGACIÓN ENTRE SECCIONES DEL MENÚ
// ===================================================================
function navegarA(seccion) {
  const secciones = ['productos', 'categorias', 'pedidos', 'admin'];
  
  if (seccion === 'inicio') {
    // En inicio mostramos productos y subimos al hero
    secciones.forEach(s => {
      const el = document.getElementById(`section-${s}`);
      if (el) el.classList.remove('d-none');
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
    actualizarNavActivo('nav-inicio');
    return;
  }

  secciones.forEach(s => {
    const el = document.getElementById(`section-${s}`);
    if (el) {
      if (s === seccion) {
        el.classList.remove('d-none');
      } else {
        el.classList.add('d-none');
      }
    }
  });

  actualizarNavActivo(`nav-${seccion}`);
  const target = document.getElementById(`section-${seccion}`);
  if (target) {
    target.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}

function actualizarNavActivo(navId) {
  document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
    link.classList.remove('active');
  });
  const activo = document.getElementById(navId);
  if (activo) activo.classList.add('active');
}

function accionSalir() {
  if (!confirm('¿Deseas salir? Se vaciará el carrito y se cerrará la sesión del usuario actual.')) return;

  // Finalizar la sesión: vaciar carrito (también en localStorage) y deseleccionar el usuario
  vaciarCarrito();
  const selCarrito = document.getElementById('select-usuario-carrito');
  if (selCarrito) selCarrito.selectedIndex = -1;

  // Cerrar el panel del carrito si estaba abierto
  const offcanvasEl = document.getElementById('cartOffcanvas');
  if (offcanvasEl && window.bootstrap) {
    const panel = bootstrap.Offcanvas.getInstance(offcanvasEl);
    if (panel) panel.hide();
  }

  mostrarAlerta('Has cerrado sesión exitosamente. ¡Gracias por visitar Sibelius Coffee & Tea!', 'info');
  navegarA('inicio');
}

// ===================================================================
// 1) GESTIÓN DE PRODUCTOS (GET, POST, PUT, DELETE)
// ===================================================================
async function cargarProductos() {
  const container = document.getElementById('productos-container');
  container.innerHTML = `
    <div class="col-12 text-center py-5">
      <div class="spinner-border text-warning" role="status"></div>
      <p class="mt-2 text-muted">Cargando catálogo gourmet desde la API...</p>
    </div>
  `;

  try {
    const res = await fetch(`${API_BASE}/api/productos`);
    if (!res.ok) throw new Error(`HTTP error ${res.status}`);
    productosGlobal = await res.json();
    renderizarProductos(productosGlobal);
  } catch (error) {
    console.error('Error al obtener productos:', error);
    container.innerHTML = `
      <div class="col-12">
        <div class="alert alert-danger text-center">
          <h5><i class="bi bi-wifi-off me-2"></i>No se pudo conectar con la API REST</h5>
          <p class="mb-2">Asegurate de que el backend de Spring Boot esté ejecutándose en <code>http://localhost:8080</code></p>
          <button class="btn btn-outline-danger btn-sm" onclick="cargarProductos()"><i class="bi bi-arrow-clockwise"></i> Reintentar</button>
        </div>
      </div>
    `;
  }
}

function renderizarProductos(productos) {
  const container = document.getElementById('productos-container');
  container.innerHTML = '';

  if (!productos || productos.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center py-5">
        <i class="bi bi-inbox fs-1 text-muted"></i>
        <p class="text-muted mt-2">No se encontraron productos en esta selección.</p>
        <button class="btn btn-outline-secondary btn-sm" onclick="cargarProductos()">Ver todos</button>
      </div>
    `;
    return;
  }

  productos.forEach(p => {
    const catNombre = p.categoria ? p.categoria.nombre : 'Gourmet';
    const tipo = p.tipo || 'General';
    const imagen = p.imagenUrl || obtenerImagenPorDefecto(tipo);
    const stock = p.stock != null ? p.stock : 0;
    
    // Badge de stock con colores semánticos
    let stockBadgeClass = 'bg-success';
    let stockTexto = `${stock} en stock`;
    if (stock === 0) {
      stockBadgeClass = 'bg-danger';
      stockTexto = 'Agotado';
    } else if (stock <= 5) {
      stockBadgeClass = 'bg-danger';
      stockTexto = `¡Solo ${stock} restantes!`;
    } else if (stock <= 10) {
      stockBadgeClass = 'bg-warning text-dark';
    }

    // Badge de tipo (Polimorfismo)
    let badgeTipoClass = 'bg-dark';
    if (tipo === 'Bebida') badgeTipoClass = 'bg-primary';
    if (tipo === 'Comida') badgeTipoClass = 'bg-warning text-dark';

    // Detalle específico (Polimorfismo)
    let detalleEspecificoHTML = '';
    if (p.volumenMl != null) {
      const frioCalor = p.esFria ? 'Fría' : 'Caliente';
      detalleEspecificoHTML = `<span class="specific-details"><i class="bi bi-cup-straw me-1"></i>${p.volumenMl}ml (${frioCalor})</span>`;
    } else if (p.pesoGramos != null) {
      detalleEspecificoHTML = `<span class="specific-details"><i class="bi bi-basket me-1"></i>${p.pesoGramos}g | Vto: ${p.fechaVencimiento || 'N/D'}</span>`;
    }

    const desc = p.descripcion || 'Producto artesanal de alta calidad.';
    const shortDesc = desc.length > 70 ? desc.substring(0, 70) + '...' : desc;

    const card = document.createElement('div');
    card.className = 'col';
    card.innerHTML = `
      <div class="product-card">
        <div class="product-img-wrapper">
          <img src="${imagen}" alt="${p.nombre}" onerror="this.src='${obtenerImagenPorDefecto(tipo)}'" />
          <span class="badge ${badgeTipoClass} badge-tipo">${tipo}</span>
          <span class="badge ${stockBadgeClass} badge-stock">${stockTexto}</span>
        </div>
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-1">
            <span class="product-category-name">${catNombre}</span>
            <small class="text-muted fw-bold">#${p.id}</small>
          </div>
          <h5 class="card-title">${p.nombre}</h5>
          
          <p class="card-text short-description mb-1">${shortDesc}</p>
          <p class="card-text full-description mb-1" style="display: none;">${desc}</p>
          ${desc.length > 70 ? `<button class="btn btn-link btn-sm p-0 mb-2 text-start text-decoration-none" onclick="toggleDescripcion(this)">Ver más</button>` : ''}
          
          ${detalleEspecificoHTML}

          <div class="d-flex justify-content-between align-items-center mt-auto pt-3 border-top">
            <div class="price-tag">$${Number(p.precio).toFixed(2)}</div>
            <div class="btn-group">
              <button class="btn btn-primary-custom btn-sm px-3" 
                      onclick="addToCart(${p.id}, '${escapeHtml(p.nombre)}', ${p.precio}, '${imagen}', ${stock}, this)"
                      ${stock === 0 ? 'disabled' : ''}>
                <i class="bi bi-cart-plus me-1"></i>${stock === 0 ? 'Sin stock' : 'Agregar'}
              </button>
              <button class="btn btn-outline-secondary btn-sm" title="Editar" onclick="abrirModalEditarProducto(${p.id})">
                <i class="bi bi-pencil"></i>
              </button>
              <button class="btn btn-outline-danger btn-sm" title="Eliminar" onclick="confirmarEliminarProducto(${p.id}, '${escapeHtml(p.nombre)}')">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    `;
    container.appendChild(card);
  });
}

function toggleDescripcion(btn) {
  const shortP = btn.previousElementSibling.previousElementSibling;
  const fullP = btn.previousElementSibling;
  if (fullP.style.display === 'none') {
    fullP.style.display = 'block';
    shortP.style.display = 'none';
    btn.textContent = 'Ver menos';
  } else {
    fullP.style.display = 'none';
    shortP.style.display = 'block';
    btn.textContent = 'Ver más';
  }
}

function filtrarProductosPorTexto() {
  const query = document.getElementById('input-busqueda-producto').value.trim().toLowerCase();
  const catId = Number(document.getElementById('select-filtro-categoria').value);
  
  let filtrados = productosGlobal.filter(p => {
    const coincideTexto = p.nombre.toLowerCase().includes(query) || 
                          (p.descripcion && p.descripcion.toLowerCase().includes(query));
    const coincideCat = (catId === 0) || (p.categoria && p.categoria.id === catId);
    return coincideTexto && coincideCat;
  });

  renderizarProductos(filtrados);
}

function filtrarProductosPorCategoria() {
  filtrarProductosPorTexto();
}

function obtenerImagenPorDefecto(tipo) {
  if (tipo === 'Bebida') return 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80';
  if (tipo === 'Comida') return 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=600&auto=format&fit=crop&q=80';
  return 'https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=600&auto=format&fit=crop&q=80';
}

// ===================================================================
// MODAL: FORMULARIO AGREGAR / EDITAR PRODUCTO (POST & PUT)
// ===================================================================
function abrirModalNuevoProducto() {
  document.getElementById('form-producto').reset();
  document.getElementById('prod-id').value = '';
  document.getElementById('modalProductoTitulo').innerHTML = '<i class="bi bi-box-seam me-2"></i>Registrar Nuevo Producto';
  document.getElementById('prod-tipo').value = 'General';
  alternarCamposPorTipo();
  
  const modal = new bootstrap.Modal(document.getElementById('modalProducto'));
  modal.show();
}

async function abrirModalEditarProducto(id) {
  try {
    const res = await fetch(`${API_BASE}/api/productos/${id}`);
    if (!res.ok) throw new Error('Producto no encontrado');
    const p = await res.json();

    document.getElementById('prod-id').value = p.id;
    document.getElementById('prod-nombre').value = p.nombre;
    document.getElementById('prod-descripcion').value = p.descripcion || '';
    document.getElementById('prod-precio').value = p.precio;
    document.getElementById('prod-stock').value = p.stock;
    document.getElementById('prod-imagen').value = p.imagenUrl || '';
    if (p.categoria && p.categoria.id) {
      document.getElementById('prod-categoria').value = p.categoria.id;
    }

    const tipo = p.tipo || 'General';
    document.getElementById('prod-tipo').value = tipo;
    alternarCamposPorTipo();

    if (tipo === 'Bebida') {
      document.getElementById('prod-volumen').value = p.volumenMl || 250;
      document.getElementById('prod-esfria').value = p.esFria ? 'true' : 'false';
    } else if (tipo === 'Comida') {
      document.getElementById('prod-peso').value = p.pesoGramos || 100.0;
      document.getElementById('prod-vencimiento').value = p.fechaVencimiento || '';
    }

    document.getElementById('modalProductoTitulo').innerHTML = `<i class="bi bi-pencil-square me-2"></i>Editar Producto #${p.id}`;
    const modal = new bootstrap.Modal(document.getElementById('modalProducto'));
    modal.show();
  } catch (err) {
    mostrarAlerta('No se pudo cargar el producto para editar: ' + err.message, 'danger');
  }
}

function alternarCamposPorTipo() {
  const tipo = document.getElementById('prod-tipo').value;
  const campoBebidaVol = document.getElementById('campo-bebida-volumen');
  const campoBebidaFria = document.getElementById('campo-bebida-fria');
  const campoComidaPeso = document.getElementById('campo-comida-peso');
  const campoComidaVto = document.getElementById('campo-comida-vto');

  campoBebidaVol.classList.add('d-none');
  campoBebidaFria.classList.add('d-none');
  campoComidaPeso.classList.add('d-none');
  campoComidaVto.classList.add('d-none');

  if (tipo === 'Bebida') {
    campoBebidaVol.classList.remove('d-none');
    campoBebidaFria.classList.remove('d-none');
  } else if (tipo === 'Comida') {
    campoComidaPeso.classList.remove('d-none');
    campoComidaVto.classList.remove('d-none');
  }
}

async function guardarProducto(event) {
  event.preventDefault();
  const id = document.getElementById('prod-id').value;
  const tipo = document.getElementById('prod-tipo').value;
  
  const payload = {
    nombre: document.getElementById('prod-nombre').value.trim(),
    descripcion: document.getElementById('prod-descripcion').value.trim(),
    precio: parseFloat(document.getElementById('prod-precio').value),
    stock: parseInt(document.getElementById('prod-stock').value, 10),
    categoriaId: parseInt(document.getElementById('prod-categoria').value, 10),
    imagenUrl: document.getElementById('prod-imagen').value.trim(),
    tipo: tipo
  };

  if (tipo === 'Bebida') {
    payload.volumenMl = parseInt(document.getElementById('prod-volumen').value || '250', 10);
    payload.esFria = (document.getElementById('prod-esfria').value === 'true');
  } else if (tipo === 'Comida') {
    payload.pesoGramos = parseFloat(document.getElementById('prod-peso').value || '100');
    payload.fechaVencimiento = document.getElementById('prod-vencimiento').value || 'N/D';
  }

  try {
    let url = `${API_BASE}/api/productos`;
    let method = 'POST';

    if (id) {
      url += `/${id}`;
      method = 'PUT';
    }

    const res = await fetch(url, {
      method: method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const errorJson = await res.json().catch(() => ({}));
      throw new Error(errorJson.mensaje || `Error al guardar producto (${res.status})`);
    }

    // Cerrar modal y refrescar
    const modalEl = document.getElementById('modalProducto');
    const modalInstance = bootstrap.Modal.getInstance(modalEl);
    if (modalInstance) modalInstance.hide();

    mostrarAlerta(id ? '¡Producto actualizado con éxito!' : '¡Producto creado y guardado en la base de datos!', 'success');
    await cargarProductos();
    await cargarAdmin();
  } catch (err) {
    mostrarAlerta('Error: ' + err.message, 'danger');
  }
}

async function confirmarEliminarProducto(id, nombre) {
  if (confirm(`¿Estás seguro de que deseas eliminar permanentemente el producto '${nombre}' (ID: ${id}) de la base de datos?`)) {
    try {
      const res = await fetch(`${API_BASE}/api/productos/${id}`, { method: 'DELETE' });
      if (!res.ok) {
        // Mostrar el mensaje de la API (ej: 409 si el producto ya forma parte de pedidos)
        const data = await res.json().catch(() => ({}));
        throw new Error(data.mensaje || `No se pudo eliminar (${res.status})`);
      }
      mostrarAlerta(`Producto '${nombre}' eliminado exitosamente.`, 'success');
      await cargarProductos();
      await cargarAdmin();
    } catch (err) {
      mostrarAlerta('Error al eliminar producto: ' + err.message, 'danger');
    }
  }
}

// Búsqueda Modal por ID
function abrirModalBuscarId(paraEditar = false, paraEliminar = false) {
  document.getElementById('input-buscar-id-modal').value = '';
  document.getElementById('resultado-busqueda-id').innerHTML = '';
  const modal = new bootstrap.Modal(document.getElementById('modalBuscarId'));
  modal.show();
}

async function ejecutarBusquedaPorIdModal() {
  const id = document.getElementById('input-buscar-id-modal').value.trim();
  const divRes = document.getElementById('resultado-busqueda-id');
  if (!id) {
    divRes.innerHTML = '<div class="alert alert-warning small">Por favor ingresa un número de ID.</div>';
    return;
  }

  divRes.innerHTML = '<div class="spinner-border spinner-border-sm text-secondary"></div> Buscando...';
  try {
    const res = await fetch(`${API_BASE}/api/productos/${id}`);
    if (!res.ok) throw new Error('Producto no encontrado');
    const p = await res.json();
    divRes.innerHTML = `
      <div class="card p-3 border-success">
        <h6 class="fw-bold">${p.nombre} (ID: ${p.id})</h6>
        <p class="small text-muted mb-1">${p.descripcion || ''}</p>
        <div><strong>Precio:</strong> $${p.precio} | <strong>Stock:</strong> ${p.stock} | <strong>Tipo:</strong> ${p.tipo || 'General'}</div>
        <div class="mt-3 d-flex gap-2">
          <button class="btn btn-warning btn-sm" onclick="abrirModalEditarProducto(${p.id})"><i class="bi bi-pencil"></i> Modificar</button>
          <button class="btn btn-danger btn-sm" onclick="confirmarEliminarProducto(${p.id}, '${escapeHtml(p.nombre)}')"><i class="bi bi-trash"></i> Eliminar</button>
        </div>
      </div>
    `;
  } catch (err) {
    divRes.innerHTML = `<div class="alert alert-danger small">No se encontró ningún producto con ID ${id}.</div>`;
  }
}

// ===================================================================
// 2) GESTIÓN DE CATEGORÍAS
// ===================================================================
async function cargarCategorias() {
  try {
    const res = await fetch(`${API_BASE}/api/categorias`);
    if (!res.ok) return;
    categoriasGlobal = await res.json();

    // Actualizar select de filtros y select del modal
    const selFiltro = document.getElementById('select-filtro-categoria');
    const selProd = document.getElementById('prod-categoria');
    
    selFiltro.innerHTML = '<option value="0">Todas las categorías</option>';
    selProd.innerHTML = '';

    categoriasGlobal.forEach(c => {
      selFiltro.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
      selProd.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
    });

    // Renderizar tarjetas de categorías
    const catContainer = document.getElementById('categorias-container');
    if (catContainer) {
      catContainer.innerHTML = '';
      categoriasGlobal.forEach(c => {
        catContainer.innerHTML += `
          <div class="col-md-6 col-lg-3">
            <div class="card h-100 p-3 border-0 shadow-sm text-center">
              <i class="bi bi-tag-fill fs-2 text-warning mb-2"></i>
              <h5 class="fw-bold">${c.nombre}</h5>
              <p class="small text-muted">${c.descripcion || ''}</p>
              <button class="btn btn-outline-dark btn-sm mt-auto" onclick="filtrarPorCategoriaEspecifica(${c.id})">Ver productos</button>
            </div>
          </div>
        `;
      });
    }
  } catch (e) {
    console.error('Error cargando categorías', e);
  }
}

function filtrarPorCategoriaEspecifica(catId) {
  document.getElementById('select-filtro-categoria').value = catId;
  navegarA('productos');
  filtrarProductosPorCategoria();
}

function abrirModalNuevaCategoria() {
  const nombre = prompt('Ingrese el nombre de la nueva categoría:');
  if (!nombre) return;
  const descripcion = prompt('Ingrese una breve descripción:');
  
  fetch(`${API_BASE}/api/categorias`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nombre, descripcion })
  })
  .then(res => res.json())
  .then(nueva => {
    mostrarAlerta(`Categoría '${nueva.nombre}' creada con éxito.`, 'success');
    cargarCategorias();
  })
  .catch(err => mostrarAlerta('Error creando categoría: ' + err.message, 'danger'));
}

// ===================================================================
// 3) GESTIÓN DE USUARIOS
// ===================================================================
async function cargarUsuarios() {
  try {
    const res = await fetch(`${API_BASE}/api/usuarios`);
    if (!res.ok) return;
    usuariosGlobal = await res.json();

    const selCarrito = document.getElementById('select-usuario-carrito');
    const selFiltroPed = document.getElementById('select-filtro-usuario-pedidos');
    const tablaUsers = document.getElementById('tabla-usuarios-body');

    selCarrito.innerHTML = '';
    selFiltroPed.innerHTML = '<option value="0">Todos los usuarios</option>';
    if (tablaUsers) tablaUsers.innerHTML = '';

    usuariosGlobal.forEach(u => {
      // Por defecto seleccionar al usuario 5 (Cliente Gourmet Demo de los ejemplos)
      const selected = (u.id === 5) ? 'selected' : '';
      selCarrito.innerHTML += `<option value="${u.id}" ${selected}>#${u.id} - ${u.nombre} (${u.email})</option>`;
      selFiltroPed.innerHTML += `<option value="${u.id}">#${u.id} - ${u.nombre}</option>`;

      if (tablaUsers) {
        tablaUsers.innerHTML += `
          <tr>
            <td><strong>#${u.id}</strong></td>
            <td>${u.nombre}</td>
            <td>${u.email}</td>
            <td><span class="badge ${u.rol === 'ADMIN' ? 'bg-danger' : 'bg-secondary'}">${u.rol}</span></td>
            <td>
              <button class="btn btn-sm btn-outline-primary" onclick="verPedidosDeUsuario(${u.id})">
                <i class="bi bi-clock-history"></i> Pedidos
              </button>
            </td>
          </tr>
        `;
      }
    });
  } catch (e) {
    console.error('Error cargando usuarios', e);
  }
}

function abrirModalNuevoUsuario() {
  const nombre = prompt('Nombre completo del usuario:');
  if (!nombre) return;
  const email = prompt('Email del usuario:');
  if (!email) return;

  fetch(`${API_BASE}/api/usuarios`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nombre, email, rol: 'CLIENTE' })
  })
  .then(res => res.json())
  .then(() => {
    mostrarAlerta('Usuario registrado exitosamente', 'success');
    cargarUsuarios();
  })
  .catch(err => mostrarAlerta('Error al crear usuario: ' + err.message, 'danger'));
}

// ===================================================================
// 4) CARRITO DE COMPRAS Y REALIZAR PEDIDO (POST /api/pedidos)
// ===================================================================
function addToCart(id, title, price, image, stock, button) {
  let existing = cart.find(item => item.id === id);
  if (existing) {
    if (existing.quantity >= stock) {
      mostrarAlerta(`No puedes agregar más unidades. El stock máximo disponible es ${stock}.`, 'warning');
      return;
    }
    existing.quantity++;
  } else {
    cart.push({ id, title, price, image, stock, quantity: 1 });
  }

  guardarCarrito();
  updateCartUI();

  // Feedback en el botón
  if (button) {
    const originalText = button.innerHTML;
    button.innerHTML = '<i class="bi bi-check-lg"></i> Agregado';
    button.classList.replace('btn-primary-custom', 'btn-success');
    setTimeout(() => {
      button.innerHTML = originalText;
      button.classList.replace('btn-success', 'btn-primary-custom');
    }, 800);
  }
}

function changeQuantity(id, delta) {
  let item = cart.find(i => i.id === id);
  if (!item) return;

  if (delta > 0 && item.quantity >= item.stock) {
    mostrarAlerta(`Stock límite alcanzado (${item.stock} unidades).`, 'warning');
    return;
  }

  item.quantity += delta;
  if (item.quantity <= 0) {
    cart = cart.filter(i => i.id !== id);
  }
  guardarCarrito();
  updateCartUI();
}

function removeFromCart(id) {
  cart = cart.filter(i => i.id !== id);
  guardarCarrito();
  updateCartUI();
}

function vaciarCarrito() {
  cart = [];
  guardarCarrito();
  updateCartUI();
}

function guardarCarrito() {
  localStorage.setItem('techlab_cart', JSON.stringify(cart));
}

function updateCartUI() {
  const container = document.getElementById('carrito-items');
  const counterNav = document.getElementById('cart-counter-nav');
  const counter = document.getElementById('cart-counter');
  const totalEl = document.getElementById('carrito-total');
  const btnComprar = document.getElementById('btn-realizar-compra');

  const totalCantidad = cart.reduce((sum, i) => sum + i.quantity, 0);
  counterNav.textContent = totalCantidad;
  counter.textContent = totalCantidad;

  if (cart.length === 0) {
    container.innerHTML = '<div class="text-center text-muted my-5"><i class="bi bi-cart-x fs-1 d-block mb-2"></i>Tu carrito está vacío</div>';
    totalEl.textContent = '$0.00';
    btnComprar.disabled = true;
    return;
  }

  btnComprar.disabled = false;
  container.innerHTML = '';
  let totalMonto = 0;

  cart.forEach(item => {
    const subtotal = item.price * item.quantity;
    totalMonto += subtotal;

    container.innerHTML += `
      <div class="cart-item-row">
        <img src="${item.image}" alt="${item.title}" class="cart-item-img" />
        <div class="flex-grow-1">
          <h6 class="mb-0 fw-bold fs-6">${item.title}</h6>
          <small class="text-muted">$${item.price.toFixed(2)} c/u</small>
          <div class="d-flex align-items-center justify-content-between mt-2">
            <div class="d-flex align-items-center gap-1">
              <button class="btn btn-sm btn-outline-secondary cart-quantity-btn" onclick="changeQuantity(${item.id}, -1)">-</button>
              <span class="px-2 fw-bold">${item.quantity}</span>
              <button class="btn btn-sm btn-outline-secondary cart-quantity-btn" onclick="changeQuantity(${item.id}, 1)">+</button>
            </div>
            <span class="fw-bold text-success">$${subtotal.toFixed(2)}</span>
          </div>
        </div>
        <button class="btn btn-sm text-danger p-0" title="Eliminar" onclick="removeFromCart(${item.id})">
          <i class="bi bi-x-circle fs-5"></i>
        </button>
      </div>
    `;
  });

  totalEl.textContent = `$${totalMonto.toFixed(2)}`;
}

function realizarPedidoDesdeBoton() {
  if (cart.length === 0) {
    mostrarAlerta('Tu carrito está vacío. Agrega productos antes de realizar un pedido.', 'warning');
    const offcanvas = new bootstrap.Offcanvas(document.getElementById('cartOffcanvas'));
    offcanvas.show();
    return;
  }
  const offcanvas = new bootstrap.Offcanvas(document.getElementById('cartOffcanvas'));
  offcanvas.show();
}

/**
 * Envío atómico del pedido a la API REST (POST /api/pedidos)
 * Maneja la excepción StockInsuficienteException en caso de error 400 Bad Request
 */
async function realizarPedidoAPI() {
  if (cart.length === 0) return;

  const usuarioId = parseInt(document.getElementById('select-usuario-carrito').value, 10);
  if (!usuarioId) {
    mostrarAlerta('Por favor selecciona un usuario para asociar al pedido.', 'warning');
    return;
  }

  const itemsPedido = cart.map(i => ({
    productoId: i.id,
    cantidad: i.quantity
  }));

  const requestPayload = {
    usuarioId: usuarioId,
    itemsPedido: itemsPedido
  };

  const btnComprar = document.getElementById('btn-realizar-compra');
  btnComprar.disabled = true;
  btnComprar.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

  try {
    const res = await fetch(`${API_BASE}/api/pedidos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestPayload)
    });

    const data = await res.json();

    if (!res.ok) {
      // Captura y comunicación clara de StockInsuficienteException u otras fallas
      throw new Error(data.mensaje || data.error || 'Ocurrió un error al procesar el pedido.');
    }

    // Éxito: Cerrar offcanvas y mostrar modal de confirmación
    const offcanvasEl = document.getElementById('cartOffcanvas');
    const offcanvasInstance = bootstrap.Offcanvas.getInstance(offcanvasEl);
    if (offcanvasInstance) offcanvasInstance.hide();

    document.getElementById('modal-pedido-id').textContent = `#${String(data.id).padStart(4, '0')}`;
    document.getElementById('modal-total').textContent = `$${Number(data.total).toFixed(2)}`;
    
    const exitoModal = new bootstrap.Modal(document.getElementById('compraExitosaModal'));
    exitoModal.show();

    // Vaciar carrito
    vaciarCarrito();

    // Actualizar catálogo de productos (descuento automático de stock) y pedidos
    await cargarProductos();
    await cargarPedidos();
    await cargarAdmin();

  } catch (error) {
    console.error('Error al realizar pedido:', error);
    mostrarAlerta(`[ERROR EN PEDIDO]: ${error.message}`, 'danger');
  } finally {
    btnComprar.disabled = false;
    btnComprar.innerHTML = '<i class="bi bi-bag-check-fill me-2"></i>Realizar Pedido';
  }
}

// ===================================================================
// 5) CONSULTAR HISTORIAL DE PEDIDOS Y GESTIÓN DE ESTADOS
// ===================================================================
async function cargarPedidos() {
  const tbody = document.getElementById('tabla-pedidos-body');
  if (!tbody) return;

  const selFiltro = document.getElementById('select-filtro-usuario-pedidos');
  const usuarioId = selFiltro ? parseInt(selFiltro.value, 10) : 0;

  let url = `${API_BASE}/api/pedidos`;
  if (usuarioId > 0) {
    url = `${API_BASE}/api/usuarios/${usuarioId}/pedidos`;
  }

  tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4"><div class="spinner-border spinner-border-sm text-secondary"></div> Cargando historial...</td></tr>';

  try {
    const res = await fetch(url);
    if (!res.ok) throw new Error('Error al consultar pedidos');
    const pedidos = await res.json();

    tbody.innerHTML = '';
    if (!pedidos || pedidos.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No se registran pedidos en este criterio.</td></tr>';
      return;
    }

    pedidos.forEach(p => {
      // Badge de estado
      const estadoBadge = obtenerBadgeEstado(p.estado);
      const itemsTexto = (p.items || []).map(i => {
        const prodNombre = i.producto ? i.producto.nombre : 'Producto';
        return `<div><span class="badge bg-light text-dark border me-1">${i.cantidad}x</span> ${prodNombre}</div>`;
      }).join('');

      tbody.innerHTML += `
        <tr>
          <td><strong>#${String(p.id).padStart(4, '0')}</strong></td>
          <td><small>${p.fecha || ''}</small></td>
          <td>${p.usuario ? p.usuario.nombre : 'Cliente'}</td>
          <td>${itemsTexto}</td>
          <td><strong class="text-success">$${Number(p.total).toFixed(2)}</strong></td>
          <td>${estadoBadge}</td>
          <td>
            <select class="form-select form-select-sm w-auto" onchange="cambiarEstadoPedido(${p.id}, this.value)">
              <option value="PENDIENTE" ${p.estado === 'PENDIENTE' ? 'selected' : ''}>Pendiente</option>
              <option value="CONFIRMADO" ${p.estado === 'CONFIRMADO' ? 'selected' : ''}>Confirmado</option>
              <option value="ENVIADO" ${p.estado === 'ENVIADO' ? 'selected' : ''}>Enviado</option>
              <option value="ENTREGADO" ${p.estado === 'ENTREGADO' ? 'selected' : ''}>Entregado</option>
              <option value="CANCELADO" ${p.estado === 'CANCELADO' ? 'selected' : ''}>Cancelado</option>
            </select>
          </td>
        </tr>
      `;
    });
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger py-4">No se pudo cargar el historial de pedidos (${err.message}).</td></tr>`;
  }
}

function verPedidosDeUsuario(usuarioId) {
  document.getElementById('select-filtro-usuario-pedidos').value = usuarioId;
  navegarA('pedidos');
  cargarPedidos();
}

async function cambiarEstadoPedido(pedidoId, nuevoEstado) {
  try {
    const res = await fetch(`${API_BASE}/api/pedidos/${pedidoId}/estado?nuevoEstado=${nuevoEstado}`, {
      method: 'PUT'
    });
    if (!res.ok) throw new Error('No se pudo actualizar el estado');
    mostrarAlerta(`Pedido #${pedidoId} actualizado a estado '${nuevoEstado}'.`, 'success');
    await cargarPedidos();
    await cargarProductos(); // Si se canceló, se restituye stock
    await cargarAdmin();
  } catch (err) {
    mostrarAlerta('Error al actualizar estado: ' + err.message, 'danger');
  }
}

function obtenerBadgeEstado(estado) {
  switch (estado) {
    case 'PENDIENTE': return '<span class="badge bg-warning text-dark">PENDIENTE</span>';
    case 'CONFIRMADO': return '<span class="badge bg-primary">CONFIRMADO</span>';
    case 'ENVIADO': return '<span class="badge bg-info text-dark">ENVIADO</span>';
    case 'ENTREGADO': return '<span class="badge bg-success">ENTREGADO</span>';
    case 'CANCELADO': return '<span class="badge bg-danger">CANCELADO</span>';
    default: return `<span class="badge bg-secondary">${estado}</span>`;
  }
}

// ===================================================================
// 6) ADMINISTRACIÓN (Alertas de Stock y Reportes)
// ===================================================================
async function cargarAdmin() {
  await cargarAlertasStock();
  await cargarResumenAdmin();
}

async function cargarAlertasStock() {
  const tbody = document.getElementById('tabla-stock-critico-body');
  const badgeCount = document.getElementById('badge-alertas-count');
  if (!tbody) return;

  try {
    const res = await fetch(`${API_BASE}/api/admin/alertas-stock?minimo=5`);
    if (!res.ok) return;
    const criticos = await res.json();

    if (badgeCount) badgeCount.textContent = `${criticos.length} Alertas`;
    tbody.innerHTML = '';

    if (!criticos || criticos.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="text-center text-success py-3"><i class="bi bi-shield-check me-1"></i>Inventario óptimo. No hay productos en nivel crítico de stock (&le; 5).</td></tr>';
      return;
    }

    criticos.forEach(p => {
      tbody.innerHTML += `
        <tr class="table-warning">
          <td><strong>#${p.id}</strong></td>
          <td>${p.nombre}</td>
          <td>${p.tipo || 'General'}</td>
          <td>$${Number(p.precio).toFixed(2)}</td>
          <td><span class="badge bg-danger fs-6">${p.stock}</span></td>
          <td><span class="text-danger fw-bold"><i class="bi bi-exclamation-triangle"></i> Reabastecer urgente</span></td>
          <td>
            <button class="btn btn-sm btn-success" onclick="reponerStockRapido(${p.id}, 15)">
              <i class="bi bi-plus-circle me-1"></i> +15 unidades
            </button>
          </td>
        </tr>
      `;
    });
  } catch (e) {
    console.error('Error cargando alertas de stock', e);
  }
}

async function reponerStockRapido(productoId, cantidad) {
  try {
    const res = await fetch(`${API_BASE}/api/admin/reponer-stock/${productoId}?cantidad=${cantidad}`, {
      method: 'POST'
    });
    if (!res.ok) throw new Error('Error al reponer stock');
    mostrarAlerta(`Stock reabastecido (+${cantidad} unidades) exitosamente.`, 'success');
    await cargarProductos();
    await cargarAdmin();
  } catch (err) {
    mostrarAlerta('Error: ' + err.message, 'danger');
  }
}

async function cargarResumenAdmin() {
  const kpi = document.getElementById('admin-kpi-container');
  if (!kpi) return;

  try {
    const res = await fetch(`${API_BASE}/api/admin/resumen`);
    if (!res.ok) return;
    const data = await res.json();

    kpi.innerHTML = `
      <div class="col-md-3">
        <div class="card p-3 border-0 shadow-sm text-center border-start border-4 border-primary">
          <h6 class="text-muted small">Catálogo Total</h6>
          <h3 class="fw-bold mb-0">${data.totalProductos || 0}</h3>
          <small class="text-primary">Productos activos</small>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card p-3 border-0 shadow-sm text-center border-start border-4 border-success">
          <h6 class="text-muted small">Pedidos Realizados</h6>
          <h3 class="fw-bold mb-0">${data.totalPedidos || 0}</h3>
          <small class="text-success">Órdenes registradas</small>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card p-3 border-0 shadow-sm text-center border-start border-4 border-danger">
          <h6 class="text-muted small">Alertas de Stock</h6>
          <h3 class="fw-bold mb-0 text-danger">${data.productosStockCritico || 0}</h3>
          <small class="text-danger">Stock &le; 5 unidades</small>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card p-3 border-0 shadow-sm text-center border-start border-4 border-warning">
          <h6 class="text-muted small">Usuarios Registrados</h6>
          <h3 class="fw-bold mb-0">${data.totalUsuarios || 0}</h3>
          <small class="text-muted">Clientes & Staff</small>
        </div>
      </div>
    `;
  } catch (e) {
    console.error('Error cargando KPIs', e);
  }
}

// ===================================================================
// UTILIDADES: ALERTAS Y ESCAPE HTML
// ===================================================================
function mostrarAlerta(mensaje, tipo = 'info') {
  const alertContainer = document.getElementById('system-alert-container');
  if (!alertContainer) {
    alert(mensaje);
    return;
  }

  const alertDiv = document.createElement('div');
  alertDiv.className = `alert alert-${tipo} alert-dismissible fade show shadow-sm`;
  alertDiv.role = 'alert';
  alertDiv.innerHTML = `
    <i class="bi ${tipo === 'success' ? 'bi-check-circle-fill' : (tipo === 'danger' ? 'bi-exclamation-triangle-fill' : 'bi-info-circle-fill')} me-2"></i>
    ${mensaje}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  `;

  alertContainer.prepend(alertDiv);
  setTimeout(() => {
    alertDiv.classList.remove('show');
    setTimeout(() => alertDiv.remove(), 250);
  }, 4500);
}

function escapeHtml(text) {
  if (!text) return '';
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
