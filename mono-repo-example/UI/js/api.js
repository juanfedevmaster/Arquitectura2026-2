// ── API Base URLs ──
const API = {
  AUTH:    'http://localhost:8081',
  CATALOG: 'http://localhost:8082',
  ORDERS:  'http://localhost:8083',
};

// ── Token storage ──
const Auth = {
  getToken()    { return localStorage.getItem('accessToken'); },
  getRefresh()  { return localStorage.getItem('refreshToken'); },
  getUser()     { return JSON.parse(localStorage.getItem('userData') || 'null'); },

  save(data) {
    localStorage.setItem('accessToken',  data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
  },

  saveUser(u) {
    localStorage.setItem('userData', JSON.stringify(u));
  },

  clear() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userData');
  },

  isLoggedIn() { return !!this.getToken(); },
};

// ── HTTP helpers ──
async function request(url, options = {}) {
  const token = Auth.getToken();
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(url, { ...options, headers });

  if (res.status === 401) {
    const refreshed = await tryRefresh();
    if (refreshed) {
      headers['Authorization'] = `Bearer ${Auth.getToken()}`;
      const retry = await fetch(url, { ...options, headers });
      if (!retry.ok) throw new ApiError(retry.status, await retry.text());
      return retry.status === 204 ? null : retry.json();
    } else {
      Auth.clear();
      showPage('login');
      throw new ApiError(401, 'Sesión expirada');
    }
  }

  if (!res.ok) {
    let msg = `Error ${res.status}`;
    try { const body = await res.json(); msg = body.message || body.error || msg; } catch (_) {}
    throw new ApiError(res.status, msg);
  }

  return res.status === 204 ? null : res.json();
}

async function tryRefresh() {
  const refreshToken = Auth.getRefresh();
  if (!refreshToken) return false;
  try {
    const res = await fetch(`${API.AUTH}/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
    if (!res.ok) return false;
    const data = await res.json();
    Auth.save(data);
    return true;
  } catch (_) {
    return false;
  }
}

class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

// ── Auth API ──
const AuthAPI = {
  login(username, password) {
    return request(`${API.AUTH}/auth/login`, {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
  },
  register(username, email, password) {
    return request(`${API.AUTH}/auth/register`, {
      method: 'POST',
      body: JSON.stringify({ username, email, password }),
    });
  },
};

// ── Catalog API ──
const CatalogAPI = {
  getProducts(params = {}) {
    const qs = new URLSearchParams();
    if (params.name)       qs.set('name', params.name);
    if (params.categoryId) qs.set('categoryId', params.categoryId);
    if (params.minPrice)   qs.set('minPrice', params.minPrice);
    if (params.maxPrice)   qs.set('maxPrice', params.maxPrice);
    const query = qs.toString() ? `?${qs}` : '';
    return request(`${API.CATALOG}/products${query}`);
  },

  getCategories() {
    return request(`${API.CATALOG}/categories`);
  },
};

// ── Orders API ──
const OrdersAPI = {
  getCart() {
    return request(`${API.ORDERS}/cart`);
  },

  addToCart(productId, quantity, unitPrice) {
    return request(`${API.ORDERS}/cart/items`, {
      method: 'POST',
      body: JSON.stringify({ productId, quantity, unitPrice }),
    });
  },

  removeFromCart(itemId) {
    return request(`${API.ORDERS}/cart/items/${itemId}`, { method: 'DELETE' });
  },

  confirmOrder() {
    return request(`${API.ORDERS}/orders`, { method: 'POST' });
  },

  getMyOrders() {
    return request(`${API.ORDERS}/orders`);
  },
};
