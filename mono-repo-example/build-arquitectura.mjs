import { writeFileSync } from "node:fs";
import * as k from "/Users/juanfe/.claude/skills/anatomy/kit/iso-kit.mjs";
import { kitScript } from "/Users/juanfe/.claude/skills/anatomy/scripts/inline-kit.mjs";

const theme = process.argv.includes("--light") ? "light" : "dark";
const OUT = "/Volumes/JuanFe-M2/GIT/Arquitectura2026-2/mono-repo-example/arquitectura.html";
const WIDTH = 960, HEIGHT = 620;

// ── World layout ──────────────────────────────────────────────────────────────
// x = derecha, y = profundidad (hacia atrás), z = arriba
// Rack: gabinete 19" centrado a la derecha
const RACK   = { x: 10,  y: -22, w: 68, d: 44, r: 3 };
const RACK_B = -2, RACK_T = 130;

// Chassis de servidores (inset dentro del rack)
const SRV    = { x: RACK.x + 8, y: RACK.y + 5, w: RACK.w - 16, d: RACK.d - 9, r: 2 };
const SRV_H  = 14;

// Posiciones Z de cada servicio
const AUTH_Z = 92, CAT_Z = 68, ORD_Z = 44;

// PostgreSQL storage array (fondo del rack)
const DB_Z = 6, DB_H = 26;

// Network switch (techo del rack)
const SW_Z = AUTH_Z + SRV_H + 6, SW_H = 7;

// Workstation del navegador (izquierda)
const DESK  = { x: -128, y: -14, w: 50, d: 38, r: 4 };
const DESK_H = 8;
const MON   = { x: -120, y:  -4, w: 34, d:  5, r: 1 };
const MON_B = DESK_H, MON_H = 32;

const P = k.fitProjection(
  [
    ...k.boxCorners(RACK,  RACK_B - 6,  RACK_T + 8),
    ...k.boxCorners(DESK, -4,  DESK_H + MON_H + 8),
  ],
  WIDTH, HEIGHT,
  { pad: 36, azimuth: 38, elevation: 30 }
);

const S  = (p, s) => k.solidSvg(p, s);
const L  = (d, s) => k.lineSvg(d, s);
const D  = (pts, s) => k.dotsSvg(pts, s);
const at = (p) => k.iso(p, P);
const byD = (pts) =>
  [...pts].sort((a, b) => k.depthOf([a[0], a[1], 0], P) - k.depthOf([b[0], b[1], 0], P));

// ── Rack cabinet frame ────────────────────────────────────────────────────────
function rackFrame() {
  const o = [];

  // Sombra
  o.push(`<path class="iso-halo" d="${k.haloOf({ x: RACK.x - 4, y: RACK.y - 2, w: RACK.w + 8, d: RACK.d + 2, r: 5 }, RACK_B - 4, 6, P)}"/>`);

  // Patas (4 esquinas, orden por profundidad)
  for (const [x, y] of byD(k.corners(RACK, 14))) {
    o.push(S(k.cylinder(x, y, 5,   RACK_B - 6, 1.4, P, 20), { tone: "lo" }));
    o.push(S(k.cylinder(x, y, 3.8, RACK_B - 4.6, 4.6, P, 20), { tone: "mid" }));
  }

  // Panel trasero
  const back = { x: RACK.x + 2, y: RACK.y + RACK.d - 3, w: RACK.w - 4, d: 3, r: 1 };
  o.push(S(k.slabOf(back, RACK_B, RACK_T - RACK_B, P, 4, 0), { tone: "lo" }));

  // Paneles laterales
  o.push(S(k.slabOf({ x: RACK.x, y: RACK.y, w: 5, d: RACK.d, r: 2 },
    RACK_B, RACK_T - RACK_B, P, 5, 0), { tone: "lo" }));
  o.push(S(k.slabOf({ x: RACK.x + RACK.w - 5, y: RACK.y, w: 5, d: RACK.d, r: 2 },
    RACK_B, RACK_T - RACK_B, P, 5, 0), { tone: "lo" }));

  // Rieles de montaje izquierdo y derecho
  const lRail = { x: RACK.x + 5, y: RACK.y + 2, w: 3.5, d: RACK.d - 4, r: 0.4 };
  const rRail = { x: RACK.x + RACK.w - 8.5, y: RACK.y + 2, w: 3.5, d: RACK.d - 4, r: 0.4 };
  o.push(S(k.slabOf(lRail, RACK_B + 4, RACK_T - RACK_B - 8, P, 2, 0.2), { tone: "faint" }));
  o.push(S(k.slabOf(rRail, RACK_B + 4, RACK_T - RACK_B - 8, P, 2, 0.2), { tone: "faint" }));

  // Agujeros de montaje en riel izquierdo
  for (const z of [15, 25, 35, 45, 55, 65, 75, 85, 95, 105, 115]) {
    o.push(D([at([lRail.x + 1.75, lRail.y + lRail.d / 2, z])], { size: 0.36, tone: "faint" }));
  }

  // Canaleta de cable (spine izquierdo interior — JWT secret)
  const spineL = RACK.x + 7, spineY = RACK.y + RACK.d - 7;
  o.push(S(k.slabOf(
    { x: RACK.x + 5.5, y: RACK.y + RACK.d - 9, w: 3, d: 6, r: 0.5 },
    RACK_B + 4, RACK_T - RACK_B - 8, P, 2, 0.2
  ), { tone: "lo" }));

  // Tapa superior
  o.push(S(k.slabOf(RACK, RACK_T, 5, P, 6, 1.4), { tone: "mid" }));
  // Asas de la tapa
  for (const ox of [10, RACK.w - 22]) {
    const h = { x: RACK.x + ox, y: RACK.y - 2, w: 12, d: 4, r: 2 };
    o.push(S(k.slabOf(h, RACK_T + 5, 5, P, 3, 0.6), { tone: "mid" }));
  }
  o.push(D(k.corners(RACK, 8).map(([x, y]) => at([x, y, RACK_T + 5])), { size: 0.42, tone: "lo" }));

  // Base inferior
  o.push(S(k.slabOf(RACK, RACK_B - 2, 4, P, 6, 1.0), { tone: "mid" }));

  // Tira de regla en panel derecho exterior
  const strip = { x: RACK.x + RACK.w - 2, y: RACK.y + 1, w: 2.5, d: RACK.d - 2, r: 0.4 };
  o.push(S(k.slabOf(strip, RACK_B + 4, RACK_T - RACK_B - 8, P, 2), { tone: "lo", crease: "none" }));
  for (const z of [20, 40, 60, 80, 100, 120]) {
    o.push(D([at([strip.x + 1.25, strip.y + strip.d / 2, z])], { size: 0.32, tone: "lo" }));
  }

  return o.join("");
}

// ── Chassis de servidor (1U) ──────────────────────────────────────────────────
function serverUnit(z, id, tone) {
  const o = [];

  // Cuerpo principal del chassis
  o.push(S(k.slabOf(SRV, z, SRV_H, P, 5, 0.8), { tone }));

  // Ranura de panel frontal
  o.push(L(k.planOutline(k.insetPlan(SRV, 3.5), z + SRV_H, P, 4), { tone: "faint" }));

  // LEDs de actividad (fila de 5)
  for (let i = 0; i < 5; i++) {
    const lx = SRV.x + 8 + i * 4.2;
    const ly = SRV.y + 4;
    const lt = (i === 0) ? (tone === "lit" ? "lit" : "hi") : "lo";
    o.push(D([at([lx, ly, z + SRV_H])], { size: 0.58, tone: lt }));
  }

  // Botón de encendido
  const pbX = SRV.x + 5, pbY = SRV.y + SRV.d / 2;
  o.push(S(k.cylinder(pbX, pbY, 2.6, z + SRV_H - 0.8, 1.6, P, 16), { tone: "mid" }));
  o.push(L(k.ring(pbX, pbY, 1.0, z + SRV_H + 0.8, P, 12), { tone: "lo" }));

  // Puerto de red (recess RJ45)
  const port = { x: SRV.x + SRV.w - 11, y: SRV.y + 4, w: 7, d: 5, r: 0.5 };
  o.push(S(k.slabOf(port, z + SRV_H - 2, 2.5, P, 2), { tone: "lo", flat: true }));

  // Placa de etiqueta
  const lbl = { x: SRV.x + 13, y: SRV.y + 2, w: SRV.w - 24, d: 7, r: 0.4 };
  o.push(S(k.slabOf(lbl, z + SRV_H, 0.8, P, 2), { tone: "lo", crease: "none" }));
  o.push(L([
    k.lineOnTop([lbl.x + 2, lbl.y + 2],   [lbl.x + lbl.w - 2, lbl.y + 2],   z + SRV_H + 0.8, P),
    k.lineOnTop([lbl.x + 2, lbl.y + 4.5], [lbl.x + lbl.w - 5, lbl.y + 4.5], z + SRV_H + 0.8, P),
  ].join(""), { tone: "faint", free: true }));

  // Remaches en esquinas
  o.push(D(k.corners(SRV, 4).map(([x, y]) => at([x, y, z + SRV_H])), { size: 0.38, tone: "lo" }));

  return `<g data-part="${id}">${o.join("")}</g>`;
}

// ── Storage array PostgreSQL (fondo del rack) ─────────────────────────────────
function dbUnit() {
  const o = [];

  o.push(S(k.slabOf(SRV, DB_Z, DB_H, P, 6, 1.0), { tone: "mid" }));

  // Bahías de disco (grilla 3×4)
  for (let row = 0; row < 3; row++) {
    for (let col = 0; col < 4; col++) {
      const tone = row === 0 ? "hi" : "lo";
      o.push(D([at([SRV.x + 8 + col * 8.5, SRV.y + 5 + row * 4.5, DB_Z + DB_H])],
        { size: 0.65, tone }));
    }
  }

  // LED de estado (brillante)
  o.push(D([at([SRV.x + 5, SRV.y + SRV.d / 2, DB_Z + DB_H])], { size: 0.9, tone: "lit" }));

  // Botón de encendido
  o.push(S(k.cylinder(SRV.x + 4.5, SRV.y + 5, 2.4, DB_Z + DB_H - 0.8, 1.6, P, 14), { tone: "mid" }));
  o.push(L(k.ring(SRV.x + 4.5, SRV.y + 5, 0.9, DB_Z + DB_H + 0.8, P, 10), { tone: "lo" }));

  // Ranura de panel
  o.push(L(k.planOutline(k.insetPlan(SRV, 4), DB_Z + DB_H, P, 5), { tone: "faint" }));

  // Placa de etiqueta
  const lbl = { x: SRV.x + 13, y: SRV.y + SRV.d - 10, w: SRV.w - 22, d: 7, r: 0.4 };
  o.push(S(k.slabOf(lbl, DB_Z + DB_H, 0.8, P, 2), { tone: "lo", crease: "none" }));
  o.push(L([
    k.lineOnTop([lbl.x + 2, lbl.y + 2],   [lbl.x + lbl.w - 2, lbl.y + 2],   DB_Z + DB_H + 0.8, P),
    k.lineOnTop([lbl.x + 2, lbl.y + 4.5], [lbl.x + lbl.w - 5, lbl.y + 4.5], DB_Z + DB_H + 0.8, P),
  ].join(""), { tone: "faint", free: true }));

  // Remaches
  o.push(D(k.corners(SRV, 4).map(([x, y]) => at([x, y, DB_Z + DB_H])), { size: 0.42, tone: "lo" }));

  return `<g data-part="db">${o.join("")}</g>`;
}

// ── Switch de red (techo del rack) ────────────────────────────────────────────
function switchUnit() {
  const o = [];

  o.push(S(k.slabOf(SRV, SW_Z, SW_H, P, 4, 0.6), { tone: "mid" }));

  // Puertos de red (8 LEDs)
  for (let i = 0; i < 8; i++) {
    o.push(D([at([SRV.x + 6 + i * 5.5, SRV.y + 4, SW_Z + SW_H])],
      { size: 0.55, tone: i < 4 ? "hi" : "lo" }));
  }

  const lbl = { x: SRV.x + 12, y: SRV.y + SRV.d - 8, w: SRV.w - 22, d: 5, r: 0.4 };
  o.push(S(k.slabOf(lbl, SW_Z + SW_H, 0.6, P, 2), { tone: "lo", crease: "none" }));
  o.push(L(k.lineOnTop([lbl.x + 2, lbl.y + 2.5], [lbl.x + lbl.w - 2, lbl.y + 2.5], SW_Z + SW_H + 0.6, P),
    { tone: "faint", free: true }));

  o.push(D(k.corners(SRV, 3).map(([x, y]) => at([x, y, SW_Z + SW_H])), { size: 0.38, tone: "lo" }));

  return `<g data-part="switch">${o.join("")}</g>`;
}

// ── Workstation del navegador ─────────────────────────────────────────────────
function browserUnit() {
  const o = [];

  // Sombra bajo el escritorio
  o.push(`<path class="iso-halo" d="${k.haloOf(DESK, -4, 6, P)}"/>`);

  // Mesa
  o.push(S(k.slabOf(DESK, -4, DESK_H + 4, P, 6, 1.4), { tone: "mid" }));
  o.push(L(k.planOutline(k.insetPlan(DESK, 5), DESK_H, P), { tone: "faint" }));
  o.push(D(k.corners(DESK, 8).map(([x, y]) => at([x, y, DESK_H])), { size: 0.42, tone: "lo" }));

  // Patas de la mesa
  for (const [x, y] of byD(k.corners(DESK, 8))) {
    o.push(S(k.slabOf({ x: x - 1.5, y: y - 1.5, w: 3, d: 3, r: 0.5 }, -4, DESK_H + 3, P, 2), { tone: "lo", crease: "none" }));
  }

  // Soporte del monitor
  const nX = DESK.x + DESK.w / 2 - 2, nY = DESK.y + 8;
  o.push(S(k.slabOf({ x: nX, y: nY, w: 4, d: 3, r: 0.5 }, DESK_H, 10, P, 2), { tone: "lo", crease: "none" }));
  // Base del soporte
  o.push(S(k.slabOf({ x: nX - 4, y: nY - 1, w: 12, d: 5, r: 1 }, DESK_H, 1, P, 2), { tone: "lo" }));

  // Cuerpo del monitor
  o.push(S(k.slabOf(MON, MON_B, MON_H, P, 4, 0.8), { tone: "hi" }));

  // Pantalla
  const SCR = k.insetPlan(MON, 1.5);
  o.push(S(k.slabOf(SCR, MON_B + MON_H - 0.4, 1.2, P, 3), { tone: "mid", flat: true }));
  o.push(L(k.planOutline(k.insetPlan(MON, 3.5), MON_B + MON_H + 0.8, P, 3), { tone: "lo" }));

  // Líneas de UI en la pantalla
  const sz = MON_B + MON_H + 0.9;
  const sx = SCR.x + 1.5, sy = SCR.y + 1.5, sw = SCR.w - 3;
  o.push(L([
    k.lineOnTop([sx,        sy + 1],  [sx + sw,       sy + 1],  sz, P),
    k.lineOnTop([sx,        sy + 4],  [sx + sw * 0.8, sy + 4],  sz, P),
    k.lineOnTop([sx,        sy + 7],  [sx + sw * 0.5, sy + 7],  sz, P),
    k.lineOnTop([sx,        sy + 10], [sx + sw * 0.7, sy + 10], sz, P),
    k.lineOnTop([sx,        sy + 13], [sx + sw * 0.4, sy + 13], sz, P),
  ].join(""), { tone: "lo", free: true }));

  // Teclado
  const KB = { x: DESK.x + 6, y: DESK.y + DESK.d - 14, w: DESK.w - 12, d: 10, r: 2 };
  o.push(S(k.slabOf(KB, DESK_H, 1.5, P, 3, 0.5), { tone: "lo", crease: "none" }));
  o.push(L(k.planOutline(k.insetPlan(KB, 2.5), DESK_H + 1.5, P, 2), { tone: "faint" }));
  // Teclas (puntos)
  for (let row = 0; row < 3; row++) {
    for (let col = 0; col < 7; col++) {
      o.push(D([at([KB.x + 4 + col * 5.2, KB.y + 2.5 + row * 2.8, DESK_H + 1.5])],
        { size: 0.28, tone: "faint" }));
    }
  }

  return `<g data-part="browser">${o.join("")}</g>`;
}

// ── Cable JWT secret (spine izquierdo del rack, línea lit vertical) ───────────
function jwtSpine() {
  // JWT secret: cable lit visible en la cara FRONTAL del rack
  // Posición: centro-izquierda del frente interior del rack (entre rack y servidor)
  // y = SRV.y (cara visible al viewer), x = entre left panel y servidor
  const cx = RACK.x + 10, cy = RACK.y + 1;

  // Segmentos en los GAPS entre chassis (z gaps de ~10 unidades)
  const gap1 = k.segment([cx, cy, CAT_Z + SRV_H + 1], [cx, cy, AUTH_Z - 1], P);
  const gap2 = k.segment([cx, cy, ORD_Z + SRV_H + 1], [cx, cy, CAT_Z  - 1], P);

  // Dots grandes en el nivel de CADA servicio (la conexión del secret)
  const pts = [
    at([cx, cy, AUTH_Z + SRV_H * 0.5]),
    at([cx, cy, CAT_Z  + SRV_H * 0.5]),
    at([cx, cy, ORD_Z  + SRV_H * 0.5]),
  ];

  return [
    // Glow halo
    `<path class="iso-line" data-tone="lit" style="stroke-width:5;opacity:0.2" d="${gap1 + gap2}"/>`,
    // Líneas en los gaps
    `<path class="iso-line" data-tone="lit" style="stroke-width:2.5" d="${gap1 + gap2}"/>`,
    // Dots grandes de conexión JWT en cada servicio
    D(pts, { size: 2.0, tone: "lit" }),
  ].join("");
}

// ── Cables de DB (cara frontal derecha — ruta visible al viewer) ──────────────
function dbSpine() {
  const cx = RACK.x + RACK.w - 10, cy = RACK.y + 1;
  const dbPath = k.segment([cx, cy, AUTH_Z + SRV_H * 0.5], [cx, cy, DB_Z + DB_H * 0.6], P);
  const pts = [
    at([cx, cy, AUTH_Z + SRV_H * 0.5]),
    at([cx, cy, CAT_Z  + SRV_H * 0.5]),
    at([cx, cy, ORD_Z  + SRV_H * 0.5]),
    at([cx, cy, DB_Z + DB_H * 0.6]),
  ];
  return [
    `<path class="iso-line" data-tone="lo" style="stroke-width:1.6" d="${dbPath}"/>`,
    D(pts, { size: 0.8, tone: "hi" }),
  ].join("");
}

// ── Cables de red (browser → switch → servicios) ──────────────────────────────
function networkCables() {
  // Puntos de conexión en coordenadas de pantalla
  // Browser sale por la derecha del monitor hacia el switch en la parte superior del rack
  const brOut  = at([MON.x + MON.w + 1, MON.y + 1, MON_B + MON_H * 0.6]);
  // Switch: frontal del rack (y pequeño = visible)
  const swFront = at([SRV.x + SRV.w * 0.5, SRV.y, SW_Z + SW_H * 0.5]);
  // Servicios: sus puertos frontales
  const authL  = at([SRV.x + SRV.w - 6, SRV.y, AUTH_Z + SRV_H * 0.5]);
  const catL   = at([SRV.x + SRV.w - 6, SRV.y, CAT_Z  + SRV_H * 0.5]);
  const ordL   = at([SRV.x + SRV.w - 6, SRV.y, ORD_Z  + SRV_H * 0.5]);
  const swL    = swFront;

  return {
    bs:  k.wireSvg(brOut, swL,   { tone: "lo",   ends: "both", size: 1.0 }),
    sa:  k.wireSvg(swL,   authL, { tone: "lo",   ends: "both", size: 0.9 }),
    sc:  k.wireSvg(swL,   catL,  { tone: "lo",   ends: "both", size: 0.9 }),
    so:  k.wireSvg(swL,   ordL,  { tone: "lo",   ends: "both", size: 0.9 }),
  };
}

// ── Ensamblaje SVG (orden pintor: atrás → adelante, abajo → arriba) ───────────
const cables = networkCables();

const body = [
  rackFrame(),
  dbUnit(),
  serverUnit(ORD_Z,  "orders",  "hi"),
  serverUnit(CAT_Z,  "catalog", "hi"),
  serverUnit(AUTH_Z, "auth",    "hi"),
  switchUnit(),
  browserUnit(),
  // Cables encima de todo
  `<g data-part="cables">`,
    `<g data-cable="db-spine">${dbSpine()}</g>`,
    `<g data-cable="jwt">${jwtSpine()}</g>`,
    `<g data-cable="browser-switch">${cables.bs}</g>`,
    `<g data-cable="switch-auth">${cables.sa}</g>`,
    `<g data-cable="switch-catalog">${cables.sc}</g>`,
    `<g data-cable="switch-orders">${cables.so}</g>`,
  `</g>`,
].join("");

const svg = k.figureSvg({
  width: WIDTH, height: HEIGHT,
  label: "Arquitectura Tienda de Abarrotes: tres microservicios Spring Boot (auth-service :8081, catalog-service :8082, orders-service :8083) comparten una base de datos PostgreSQL. Un navegador web consume los tres servicios directamente sin API Gateway. Una clave JWT compartida permite validación local en cada servicio.",
  body,
});

// ── Script interactivo ────────────────────────────────────────────────────────
const COMPS = {
  browser: {
    title: "UI · Navegador",
    text: "HTML + Bootstrap + JS vanilla · abre como file:// · fetch() directo a localhost sin backend propio"
  },
  switch: {
    title: "Red local · localhost",
    text: "Sin API Gateway · cada servicio escucha en su puerto · el cliente llama directo a :8081, :8082, :8083"
  },
  auth: {
    title: "auth-service · :8081",
    text: "Spring Boot · JWT accessToken 24h + refreshToken 7d · POST /auth/login · /register · /refresh · guarda usuarios en tienda_db"
  },
  catalog: {
    title: "catalog-service · :8082",
    text: "Spring Boot · GET /products público · POST /products requiere Bearer token · JwtAuthFilter valida localmente con el mismo secret"
  },
  orders: {
    title: "orders-service · :8083",
    text: "Spring Boot · POST /cart/items · GET /cart · POST /orders · JwtAuthFilter valida JWT local (mismo secret, sin llamar a auth-service)"
  },
  db: {
    title: "PostgreSQL · tienda_db · :5432",
    text: "Base de datos ÚNICA compartida · Spring JPA · tablas: users · categories · products · carts · cart_items · orders · order_items"
  },
};

// Qué cables se activan al seleccionar cada componente
const CABLE_MAP = {
  browser:  ["browser-switch"],
  switch:   ["browser-switch", "switch-auth", "switch-catalog", "switch-orders"],
  auth:     ["switch-auth", "jwt", "db-spine"],
  catalog:  ["switch-catalog", "jwt", "db-spine"],
  orders:   ["switch-orders", "jwt", "db-spine"],
  db:       ["db-spine"],
};

const TOUR_ORDER = ["browser", "auth", "catalog", "orders", "db"];

const live = `
const COMPS = ${JSON.stringify(COMPS)};
const CABLE_MAP = ${JSON.stringify(CABLE_MAP)};
const TOUR = ${JSON.stringify(TOUR_ORDER)};

const stage   = document.querySelector(".iso-stage");
const readout = document.querySelector("[data-readout]");
const still   = matchMedia("(prefers-reduced-motion: reduce)");

let selected = null, tourIdx = 0, tourTimer = null, visible = false;

function highlight(id) {
  // Opacidad de componentes
  document.querySelectorAll("[data-part]").forEach(el => {
    if (el.dataset.part === "cables") return;
    el.style.opacity = (!id || el.dataset.part === id) ? "1" : "0.35";
    el.style.transition = "opacity 0.3s";
  });
  // Cables: activar flujo en los relevantes
  document.querySelectorAll("[data-cable]").forEach(el => {
    const active = id && (CABLE_MAP[id] || []).includes(el.dataset.cable);
    el.style.opacity = active ? "1" : (id ? "0.15" : "0.7");
    el.style.transition = "opacity 0.3s";
    el.querySelectorAll(".iso-line").forEach(line => {
      if (active && !still.matches) line.setAttribute("data-flow", "");
      else line.removeAttribute("data-flow");
    });
  });
  // Readout
  if (id && COMPS[id]) {
    readout.textContent = COMPS[id].title + " · " + COMPS[id].text;
  } else {
    readout.textContent = "Haz click en un componente para ver su rol en la arquitectura";
  }
}

function select(id) {
  selected = id;
  highlight(id);
  clearTimeout(tourTimer);
  if (id) tourTimer = setTimeout(() => { selected = null; doTour(); }, 5000);
}

function doTour() {
  if (selected || still.matches) return;
  const id = TOUR[tourIdx % TOUR.length];
  tourIdx++;
  highlight(id);
  tourTimer = setTimeout(doTour, 2400);
}

// Click en un componente
stage.addEventListener("click", e => {
  const part = e.target.closest("[data-part]");
  const id = part && part.dataset.part !== "cables" ? part.dataset.part : null;
  if (id === selected) { select(null); return; }
  select(id);
});

// Cursor cambia sobre componentes interactivos
stage.addEventListener("pointermove", e => {
  const part = e.target.closest("[data-part]");
  stage.style.cursor = (part && part.dataset.part !== "cables") ? "pointer" : "default";
});

// Teclado: Tab cicla, Enter selecciona, Escape deselecciona
let focusPart = 0;
stage.setAttribute("tabindex", "0");
stage.setAttribute("role", "group");
stage.setAttribute("aria-label", "Diagrama de arquitectura. Usa Tab para navegar componentes, Enter para seleccionar.");
stage.addEventListener("keydown", e => {
  if (e.key === "Tab") {
    e.preventDefault();
    focusPart = (focusPart + (e.shiftKey ? -1 : 1) + TOUR.length) % TOUR.length;
    select(TOUR[focusPart]);
  } else if (e.key === "Enter" || e.key === " ") {
    select(TOUR[focusPart]);
  } else if (e.key === "Escape") {
    select(null);
  }
});

// IntersectionObserver: tour sólo cuando visible
new IntersectionObserver(entries => {
  visible = entries[entries.length - 1].isIntersecting;
  if (visible && !selected && !still.matches) {
    clearTimeout(tourTimer);
    tourTimer = setTimeout(doTour, 800);
  } else if (!visible) {
    clearTimeout(tourTimer);
  }
}, { rootMargin: "120px 0px" }).observe(stage);

// Estado inicial
highlight(null);
`;

// ── Card HTML ─────────────────────────────────────────────────────────────────
const cardBody = k.plateHtml({
  fig: "Fig 1",
  title: "Tienda de Abarrotes — Microservicios",
  hint: "Haz click en cada componente para ver su rol",
  readout: "Haz click en un componente para ver su rol en la arquitectura",
  keys: [
    { mark: "lit",   label: "JWT secret compartido — misma clave en auth, catalog y orders" },
    { mark: "raised", label: "Tres microservicios Spring Boot independientes en un mono-repo" },
    { mark: "flat",   label: "PostgreSQL tienda_db — base de datos única compartida" },
  ],
  caption:
    "Tres servicios Spring Boot (auth :8081, catalog :8082, orders :8083) comparten una base de datos PostgreSQL pero son deployables independientemente. " +
    "No hay API Gateway: el cliente llama a cada servicio directo. " +
    "El JWT secret es idéntico en los tres, lo que permite validación local sin llamadas entre servicios (línea dorada).",
  body: `<div class="iso-stage">${svg}</div>`,
});

writeFileSync(OUT, k.pageHtml({
  title: "Tienda de Abarrotes — Arquitectura de Microservicios",
  theme,
  body: cardBody,
  script: kitScript() + live,
  width: WIDTH + 80,
}));

console.log("✓ Escrito:", OUT);
