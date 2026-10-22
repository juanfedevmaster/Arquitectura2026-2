// ── State ──
let currentPage  = '';
let addCartProduct = null;

// ── Toast ──
function showToast(msg, type = 'success') {
  const el  = document.getElementById('toastEl');
  const msgEl = document.getElementById('toastMsg');
  el.classList.remove('toast-success', 'toast-error');
  el.classList.add(type === 'error' ? 'toast-error' : 'toast-success');
  msgEl.textContent = msg;
  bootstrap.Toast.getOrCreateInstance(el, { delay: 3200 }).show();
}

// ── Page router ──
function showPage(page) {
  document.querySelectorAll('.ta-page').forEach(p => p.classList.add('d-none'));
  const el = document.getElementById(`page-${page}`);
  if (!el) return;
  el.classList.remove('d-none');
  currentPage = page;

  const loggedIn  = Auth.isLoggedIn();
  const cartBtn   = document.getElementById('cartBtn');
  const logoutBtn = document.getElementById('logoutBtn');
  const navUser   = document.getElementById('navUsername');
  const ordersBtn = document.getElementById('ordersNavBtn');

  if (loggedIn) {
    cartBtn.style.removeProperty('display');
    logoutBtn.classList.remove('d-none');
    ordersBtn?.classList.remove('d-none');
    const user = Auth.getUser();
    if (user) { navUser.textContent = user.username; navUser.classList.remove('d-none'); }
  } else {
    cartBtn.style.setProperty('display', 'none', 'important');
    logoutBtn.classList.add('d-none');
    ordersBtn?.classList.add('d-none');
    navUser.classList.add('d-none');
  }

  // Guard: redirect to login if protected page accessed without auth
  if (['cart', 'orders', 'order-success'].includes(page) && !loggedIn) {
    showPage('login'); return;
  }
  // Guard: redirect logged-in users away from auth pages
  if (['login', 'register'].includes(page) && loggedIn) {
    showPage('catalog'); return;
  }

  // Trigger page-specific data loads
  if (page === 'cart')   loadCart();
  if (page === 'orders') loadOrders();
}

// ── Logout ──
function logout() {
  Auth.clear();
  updateCartBadge(0);
  showPage('login');
  showToast('Sesión cerrada');
}

// ── Password toggle ──
function togglePassword(inputId, btn) {
  const input = document.getElementById(inputId);
  const isHidden = input.type === 'password';
  input.type = isHidden ? 'text' : 'password';
  btn.setAttribute('aria-label', isHidden ? 'Ocultar contraseña' : 'Mostrar contraseña');
}

// ── Utility: loading state ──
function setLoading(btn, loading) {
  const text    = btn.querySelector('.btn-text');
  const spinner = btn.querySelector('.spinner-border');
  btn.disabled  = loading;
  text?.classList.toggle('d-none', loading);
  spinner?.classList.toggle('d-none', !loading);
}

// ── Login ──
document.getElementById('loginForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const form = e.target;
  form.classList.add('was-validated');
  if (!form.checkValidity()) return;

  const username = document.getElementById('loginUsername').value.trim();
  const password = document.getElementById('loginPassword').value;
  const btn      = document.getElementById('loginBtn');
  const errEl    = document.getElementById('loginError');

  setLoading(btn, true);
  errEl.classList.add('d-none');

  try {
    const data = await AuthAPI.login(username, password);
    Auth.save(data);
    Auth.saveUser({ username });
    form.classList.remove('was-validated');
    form.reset();
    showPage('catalog');
    loadProducts();
    loadCategories();
    refreshCartBadge();
    showToast(`Bienvenido, ${username}`);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.classList.remove('d-none');
  } finally {
    setLoading(btn, false);
  }
});

// ── Register ──
document.getElementById('registerForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const form = e.target;
  form.classList.add('was-validated');
  if (!form.checkValidity()) return;

  const username = document.getElementById('regUsername').value.trim();
  const email    = document.getElementById('regEmail').value.trim();
  const password = document.getElementById('regPassword').value;
  const btn      = document.getElementById('registerBtn');
  const errEl    = document.getElementById('registerError');
  const okEl     = document.getElementById('registerSuccess');

  setLoading(btn, true);
  errEl.classList.add('d-none');
  okEl.classList.add('d-none');

  try {
    await AuthAPI.register(username, email, password);
    okEl.textContent = '¡Cuenta creada exitosamente! Redirigiendo...';
    okEl.classList.remove('d-none');
    form.classList.remove('was-validated');
    form.reset();
    setTimeout(() => showPage('login'), 1800);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.classList.remove('d-none');
  } finally {
    setLoading(btn, false);
  }
});

// ── Categories ──
async function loadCategories() {
  try {
    const cats = await CatalogAPI.getCategories();
    const sel  = document.getElementById('filterCategory');
    // Remove old dynamic options
    [...sel.options].slice(1).forEach(o => o.remove());
    cats.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id;
      opt.textContent = c.name;
      sel.appendChild(opt);
    });
  } catch (_) {}
}

// ── Products ──
async function loadProducts() {
  const grid    = document.getElementById('productsGrid');
  const loading = document.getElementById('productsLoading');
  const empty   = document.getElementById('productsEmpty');

  grid.innerHTML = '';
  loading.classList.remove('d-none');
  empty.classList.add('d-none');

  const params = {
    name:       document.getElementById('filterName').value.trim()     || undefined,
    categoryId: document.getElementById('filterCategory').value        || undefined,
    minPrice:   document.getElementById('filterMin').value             || undefined,
    maxPrice:   document.getElementById('filterMax').value             || undefined,
  };

  try {
    const products = await CatalogAPI.getProducts(params);
    loading.classList.add('d-none');
    if (!products.length) { empty.classList.remove('d-none'); return; }
    products.forEach(p => grid.appendChild(buildProductCard(p)));
  } catch (err) {
    loading.classList.add('d-none');
    showToast('Error cargando productos: ' + err.message, 'error');
  }
}

function buildProductCard(p) {
  const col = document.createElement('div');
  col.className = 'col';
  const outOfStock = p.stock <= 0;
  const productJson = escAttr(JSON.stringify(p));
  col.innerHTML = `
    <div class="ta-product-card">
      <div class="ta-product-img">
        ${categoryIcon(p.categoryName)}
        <span class="ta-category-tag">${escHtml(p.categoryName || 'General')}</span>
      </div>
      <div class="ta-product-body">
        <div class="ta-product-name">${escHtml(p.name)}</div>
        <div class="ta-product-desc">${escHtml(p.description || '')}</div>
        <div class="ta-product-footer">
          <div>
            <div class="ta-product-price">${formatPrice(p.price)}</div>
            <div class="ta-product-stock">${outOfStock ? 'Sin stock' : `Stock: ${p.stock}`}</div>
          </div>
          <button class="ta-btn-add" data-product='${productJson}' ${outOfStock ? 'disabled' : ''}>
            ${outOfStock ? 'Agotado' : '+ Agregar'}
          </button>
        </div>
      </div>
    </div>`;
  col.querySelector('.ta-btn-add')?.addEventListener('click', function() {
    openAddToCart(p);
  });
  return col;
}

// ── Add to Cart Modal ──
function openAddToCart(product) {
  if (!Auth.isLoggedIn()) { showPage('login'); return; }
  addCartProduct = product;
  document.getElementById('modalProductName').textContent  = product.name;
  document.getElementById('modalProductPrice').textContent = formatPrice(product.price);
  document.getElementById('modalQty').value = 1;
  updateModalTotal();
  bootstrap.Modal.getOrCreateInstance(document.getElementById('addCartModal')).show();
}

function changeQty(delta) {
  const input = document.getElementById('modalQty');
  const val   = Math.max(1, Math.min(99, parseInt(input.value || 1) + delta));
  input.value = val;
  updateModalTotal();
}

function updateModalTotal() {
  if (!addCartProduct) return;
  const qty   = parseInt(document.getElementById('modalQty').value) || 1;
  document.getElementById('modalTotal').textContent = formatPrice(addCartProduct.price * qty);
}

// ── Cart Badge ──
async function refreshCartBadge() {
  if (!Auth.isLoggedIn()) return;
  try {
    const cart  = await OrdersAPI.getCart();
    const count = (cart.items || []).reduce((s, i) => s + i.quantity, 0);
    updateCartBadge(count);
  } catch (_) {}
}

function updateCartBadge(count) {
  const badge = document.getElementById('cartCount');
  badge.textContent = count;
  count > 0 ? badge.classList.remove('d-none') : badge.classList.add('d-none');
}

// ── Cart Page ──
async function loadCart() {
  const loading = document.getElementById('cartLoading');
  const empty   = document.getElementById('cartEmpty');
  const content = document.getElementById('cartContent');

  loading.classList.remove('d-none');
  empty.classList.add('d-none');
  content.classList.add('d-none');

  try {
    const cart = await OrdersAPI.getCart();
    loading.classList.add('d-none');
    if (!cart.items || !cart.items.length) { empty.classList.remove('d-none'); return; }
    renderCartItems(cart.items);
    content.classList.remove('d-none');
  } catch (err) {
    loading.classList.add('d-none');
    showToast('Error cargando carrito: ' + err.message, 'error');
  }
}

function renderCartItems(items) {
  const list = document.getElementById('cartItemsList');
  list.innerHTML = '';
  let total = 0;

  items.forEach(item => {
    const subtotal = item.unitPrice * item.quantity;
    total += subtotal;
    const el = document.createElement('div');
    el.className = 'ta-cart-item';
    el.innerHTML = `
      <div class="ta-cart-item-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10"/></svg>
      </div>
      <div class="ta-cart-item-info">
        <div class="ta-cart-item-name">Producto</div>
        <div class="ta-cart-item-price">${formatPrice(item.unitPrice)} × ${item.quantity}</div>
      </div>
      <div class="ta-cart-item-subtotal">${formatPrice(subtotal)}</div>
      <button class="ta-btn-remove" aria-label="Eliminar ítem">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
      </button>`;
    el.querySelector('.ta-btn-remove').addEventListener('click', () => removeCartItem(item.id));
    list.appendChild(el);
  });

  const fmt = formatPrice(total);
  document.getElementById('cartSubtotal').textContent = fmt;
  document.getElementById('cartTotal').textContent    = fmt;
}

async function removeCartItem(itemId) {
  try {
    await OrdersAPI.removeFromCart(itemId);
    showToast('Ítem eliminado');
    loadCart();
    refreshCartBadge();
  } catch (err) {
    showToast('Error: ' + err.message, 'error');
  }
}

// ── Confirm Order ──
async function confirmOrder() {
  const btn = document.getElementById('confirmBtn');
  setLoading(btn, true);
  try {
    const order = await OrdersAPI.confirmOrder();
    document.getElementById('successOrderId').textContent = `Pedido #${order.id}`;
    updateCartBadge(0);
    showPage('order-success');
  } catch (err) {
    showToast('Error al confirmar: ' + err.message, 'error');
  } finally {
    setLoading(btn, false);
  }
}

// ── My Orders ──
async function loadOrders() {
  const loading = document.getElementById('ordersLoading');
  const empty   = document.getElementById('ordersEmpty');
  const list    = document.getElementById('ordersList');

  loading.classList.remove('d-none');
  empty.classList.add('d-none');
  list.innerHTML = '';

  try {
    const orders = await OrdersAPI.getMyOrders();
    loading.classList.add('d-none');
    if (!orders.length) { empty.classList.remove('d-none'); return; }
    orders.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    orders.forEach(o => list.appendChild(buildOrderCard(o)));
  } catch (err) {
    loading.classList.add('d-none');
    showToast('Error cargando pedidos: ' + err.message, 'error');
  }
}

function buildOrderCard(order) {
  const el = document.createElement('div');
  el.className = 'ta-order-card';
  const statusClass = {
    PENDING:   'ta-status-pending',
    CONFIRMED: 'ta-status-confirmed',
    CANCELLED: 'ta-status-cancelled',
  }[order.status] || 'ta-status-pending';

  const itemsHtml = (order.items || []).map(i => `
    <div class="ta-order-item-row">
      <span>Producto</span>
      <span>${i.quantity} × ${formatPrice(i.unitPrice)}</span>
    </div>`).join('');

  el.innerHTML = `
    <div class="ta-order-header">
      <div>
        <div class="ta-order-id-text">Pedido #${escHtml(order.id)}</div>
        <div class="ta-order-date">${formatDate(order.createdAt)}</div>
      </div>
      <div class="d-flex align-items-center gap-3">
        <span class="ta-status-badge ${statusClass}">${escHtml(order.status)}</span>
        <span class="ta-order-total">${formatPrice(order.total)}</span>
      </div>
    </div>
    ${itemsHtml ? `<div class="ta-order-items-list">${itemsHtml}</div>` : ''}`;
  return el;
}

// ── Formatters & helpers ──
function formatPrice(val) {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: 'COP', maximumFractionDigits: 0,
  }).format(val);
}

function formatDate(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleDateString('es-CO', {
    year: 'numeric', month: 'long', day: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

function escHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function escAttr(str) {
  return String(str).replace(/'/g, '&#39;').replace(/"/g, '&quot;');
}

function categoryIcon(name) {
  const map = {
    'Lácteos':   '🥛', 'Carnes': '🥩', 'Frutas':    '🍎',
    'Verduras':  '🥦', 'Panadería': '🍞', 'Bebidas': '🧃',
    'Granos':    '🌾', 'Aseo':   '🧼',
  };
  const emoji = map[name] || '📦';
  return `<span style="font-size:2.5rem;line-height:1" aria-hidden="true">${emoji}</span>`;
}

// ── Init ──
(function init() {
  // Inject add-to-cart modal
  document.body.insertAdjacentHTML('beforeend', `
  <div class="modal fade" id="addCartModal" tabindex="-1" aria-labelledby="addCartModalLabel" aria-modal="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="addCartModalLabel">Agregar al carrito</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <div class="fw-700 ta-foreground" style="font-size:1.0625rem" id="modalProductName"></div>
            <div class="ta-muted mt-1" id="modalProductPrice"></div>
          </div>
          <label class="form-label ta-label">Cantidad</label>
          <div class="d-flex align-items-center gap-2 mb-3">
            <button class="ta-qty-btn" type="button" onclick="changeQty(-1)" aria-label="Reducir">−</button>
            <input type="number" class="ta-qty-input" id="modalQty" value="1" min="1" max="99" aria-label="Cantidad" />
            <button class="ta-qty-btn" type="button" onclick="changeQty(1)" aria-label="Aumentar">+</button>
          </div>
          <div class="d-flex justify-content-between align-items-center">
            <span class="ta-muted">Total</span>
            <span class="fw-700 ta-product-price" style="font-size:1.125rem" id="modalTotal"></span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn ta-btn-ghost" data-bs-dismiss="modal">Cancelar</button>
          <button class="btn ta-btn-primary" id="modalAddBtn">
            <span class="btn-text">Agregar</span>
            <span class="spinner-border spinner-border-sm d-none" role="status"></span>
          </button>
        </div>
      </div>
    </div>
  </div>`);

  document.getElementById('modalQty').addEventListener('input', updateModalTotal);
  document.getElementById('modalAddBtn').addEventListener('click', async () => {
    if (!addCartProduct) return;
    const qty = parseInt(document.getElementById('modalQty').value) || 1;
    const btn = document.getElementById('modalAddBtn');
    setLoading(btn, true);
    try {
      await OrdersAPI.addToCart(addCartProduct.id, qty, addCartProduct.price);
      bootstrap.Modal.getInstance(document.getElementById('addCartModal')).hide();
      showToast(`${addCartProduct.name} agregado al carrito`);
      refreshCartBadge();
    } catch (err) {
      showToast('Error: ' + err.message, 'error');
    } finally {
      setLoading(btn, false);
    }
  });

  // Inject "Mis pedidos" button in navbar
  const navCartBtn = document.getElementById('cartBtn');
  const ordersBtn  = document.createElement('button');
  ordersBtn.className = 'btn ta-btn-ghost d-none';
  ordersBtn.id        = 'ordersNavBtn';
  ordersBtn.innerHTML = `
    <svg xmlns="http://www.w3.org/2000/svg" width="17" height="17" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" class="me-1"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>
    Pedidos`;
  ordersBtn.addEventListener('click', () => showPage('orders'));
  navCartBtn.insertAdjacentElement('beforebegin', ordersBtn);

  // Initial route
  if (Auth.isLoggedIn()) {
    showPage('catalog');
    loadProducts();
    loadCategories();
    refreshCartBadge();
  } else {
    showPage('login');
  }
})();
