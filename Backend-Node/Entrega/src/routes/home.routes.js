// front para que sea un poco mas lindod e ver el "inicio" de la api :)

import express from 'express';

const router = express.Router();

router.get('/', (req, res) => {
  const html = `<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <meta name="description" content="API REST de Gestión de Productos con autenticación JWT y Firebase. Desarrollada por Tomás Ferrer para TalentoTech." />
  <title>API de Gestión de Productos | Tomás Ferrer</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    :root {
      --bg: #080c14;
      --card-bg: rgba(15, 23, 42, 0.8);
      --border: rgba(139, 92, 246, 0.2);
      --purple: #8b5cf6;
      --purple-light: #a78bfa;
      --purple-glow: rgba(139, 92, 246, 0.4);
      --text: #f1f5f9;
      --text-muted: #94a3b8;
      --green: #10b981;
      --blue: #3b82f6;
      --red: #ef4444;
    }

    body {
      font-family: 'Inter', sans-serif;
      background: var(--bg);
      color: var(--text);
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      position: relative;
    }

    /* Fondo animado */
    body::before {
      content: '';
      position: fixed;
      inset: 0;
      background:
        radial-gradient(ellipse 80% 60% at 50% -20%, rgba(139, 92, 246, 0.25) 0%, transparent 60%),
        radial-gradient(ellipse 60% 40% at 80% 80%, rgba(59, 130, 246, 0.12) 0%, transparent 50%);
      pointer-events: none;
      z-index: 0;
    }

    /* Grid de fondo */
    body::after {
      content: '';
      position: fixed;
      inset: 0;
      background-image:
        linear-gradient(rgba(139, 92, 246, 0.06) 1px, transparent 1px),
        linear-gradient(90deg, rgba(139, 92, 246, 0.06) 1px, transparent 1px);
      background-size: 60px 60px;
      pointer-events: none;
      z-index: 0;
    }

    .container {
      position: relative;
      z-index: 1;
      width: 100%;
      max-width: 680px;
      padding: 24px;
      animation: fadeUp 0.7s ease both;
    }

    @keyframes fadeUp {
      from { opacity: 0; transform: translateY(30px); }
      to   { opacity: 1; transform: translateY(0); }
    }

    .card {
      background: var(--card-bg);
      border: 1px solid var(--border);
      border-radius: 24px;
      padding: 48px 44px;
      backdrop-filter: blur(20px);
      box-shadow:
        0 0 0 1px rgba(139, 92, 246, 0.1),
        0 25px 60px rgba(0, 0, 0, 0.5),
        0 0 80px rgba(139, 92, 246, 0.08);
    }

    /* Badge de estado */
    .status-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      background: rgba(16, 185, 129, 0.12);
      border: 1px solid rgba(16, 185, 129, 0.3);
      color: #34d399;
      font-size: 12px;
      font-weight: 500;
      padding: 6px 14px;
      border-radius: 100px;
      margin-bottom: 28px;
      letter-spacing: 0.04em;
      text-transform: uppercase;
    }

    .status-dot {
      width: 7px;
      height: 7px;
      background: #10b981;
      border-radius: 50%;
      animation: pulse 2s infinite;
    }

    @keyframes pulse {
      0%, 100% { opacity: 1; box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.5); }
      50%       { opacity: 0.8; box-shadow: 0 0 0 5px rgba(16, 185, 129, 0); }
    }

    h1 {
      font-size: 2.1rem;
      font-weight: 800;
      line-height: 1.15;
      letter-spacing: -0.02em;
      background: linear-gradient(135deg, #f1f5f9 30%, #a78bfa 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 12px;
    }

    .author {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      color: var(--text-muted);
      font-size: 0.95rem;
      font-weight: 400;
      margin-bottom: 20px;
    }

    .author-name {
      color: var(--purple-light);
      font-weight: 600;
    }

    .divider {
      color: rgba(148, 163, 184, 0.3);
    }

    p.description {
      color: var(--text-muted);
      font-size: 0.95rem;
      line-height: 1.7;
      margin-bottom: 36px;
    }

    /* Botones */
    .btn-group {
      display: flex;
      gap: 12px;
      margin-bottom: 40px;
      flex-wrap: wrap;
    }

    .btn {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 12px 22px;
      border-radius: 12px;
      font-size: 0.9rem;
      font-weight: 600;
      text-decoration: none;
      transition: all 0.25s ease;
      cursor: pointer;
      border: none;
      font-family: 'Inter', sans-serif;
    }

    .btn-primary {
      background: linear-gradient(135deg, #7c3aed, #8b5cf6, #a78bfa);
      color: white;
      box-shadow: 0 4px 20px rgba(139, 92, 246, 0.4);
    }

    .btn-primary:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 30px rgba(139, 92, 246, 0.6);
      filter: brightness(1.1);
    }

    .btn-secondary {
      background: rgba(255, 255, 255, 0.06);
      color: var(--text);
      border: 1px solid rgba(255, 255, 255, 0.12);
      backdrop-filter: blur(8px);
    }

    .btn-secondary:hover {
      background: rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.2);
      transform: translateY(-2px);
    }

    .btn svg {
      width: 17px;
      height: 17px;
      flex-shrink: 0;
    }

    /* Endpoints */
    .endpoints-label {
      font-size: 11px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.1em;
      color: rgba(148, 163, 184, 0.5);
      margin-bottom: 14px;
    }

    .endpoints {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .endpoint {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 14px;
      background: rgba(255,255,255, 0.03);
      border: 1px solid rgba(255,255,255,0.06);
      border-radius: 10px;
      transition: border-color 0.2s;
    }

    .endpoint:hover {
      border-color: rgba(139, 92, 246, 0.3);
    }

    .method {
      font-family: 'JetBrains Mono', monospace;
      font-size: 11px;
      font-weight: 500;
      padding: 3px 8px;
      border-radius: 6px;
      min-width: 48px;
      text-align: center;
    }

    .method.get    { background: rgba(16,185,129,0.15);  color: #34d399; border: 1px solid rgba(16,185,129,0.25); }
    .method.post   { background: rgba(59,130,246,0.15);  color: #60a5fa; border: 1px solid rgba(59,130,246,0.25); }
    .method.delete { background: rgba(239,68,68,0.15);   color: #f87171; border: 1px solid rgba(239,68,68,0.25); }

    .endpoint-path {
      font-family: 'JetBrains Mono', monospace;
      font-size: 13px;
      color: var(--text-muted);
    }

    .endpoint-desc {
      font-size: 12px;
      color: rgba(148,163,184,0.5);
      margin-left: auto;
    }

    /* Footer */
    .footer {
      margin-top: 32px;
      text-align: center;
      font-size: 12px;
      color: rgba(148,163,184,0.35);
    }

    @media (max-width: 500px) {
      .card { padding: 32px 24px; }
      h1 { font-size: 1.6rem; }
      .btn-group { flex-direction: column; }
      .btn { justify-content: center; }
      .endpoint-desc { display: none; }
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="card">

      <div class="status-badge">
        <span class="status-dot"></span>
        API Online
      </div>

      <h1>API de Gestión de Productos</h1>

      <div class="author">
        <span>Desarrollado por</span>
        <span class="author-name">Tomás Ferrer</span>
        <span class="divider">·</span>
        <span>TalentoTech 2026</span>
      </div>

      <p class="description">
        API REST construida con <strong style="color:#c4b5fd">Node.js + Express 5</strong>,
        autenticación mediante <strong style="color:#c4b5fd">JWT</strong>
        y base de datos en <strong style="color:#c4b5fd">Firebase Firestore</strong>.
      </p>

      <div class="btn-group">
        <a href="/api-docs" class="btn btn-primary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
            <polyline points="10 9 9 9 8 9"/>
          </svg>
          Ver Documentación
        </a>
        <a href="https://github.com/FerrerThomas/TalentoTech/tree/main/Backend-Node/Entrega" target="_blank" rel="noopener" class="btn btn-secondary">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0 0 24 12c0-6.63-5.37-12-12-12z"/>
          </svg>
          GitHub
        </a>
      </div>

      <p class="endpoints-label">Endpoints disponibles</p>
      <div class="endpoints">
        <div class="endpoint">
          <span class="method post">POST</span>
          <span class="endpoint-path">/auth/login</span>
          <span class="endpoint-desc">Obtener token JWT</span>
        </div>
        <div class="endpoint">
          <span class="method get">GET</span>
          <span class="endpoint-path">/api/products</span>
          <span class="endpoint-desc">Listar productos</span>
        </div>
        <div class="endpoint">
          <span class="method get">GET</span>
          <span class="endpoint-path">/api/products/:id</span>
          <span class="endpoint-desc">Obtener producto</span>
        </div>
        <div class="endpoint">
          <span class="method post">POST</span>
          <span class="endpoint-path">/api/products/create</span>
          <span class="endpoint-desc">Crear producto</span>
        </div>
        <div class="endpoint">
          <span class="method delete">DELETE</span>
          <span class="endpoint-path">/api/products/:id</span>
          <span class="endpoint-desc">Eliminar producto</span>
        </div>
      </div>

    </div>
    <div class="footer">Tomas Ferrer &nbsp;·&nbsp; Node.js + Express + Firebase</div>
  </div>
</body>
</html>`;

  res.setHeader('Content-Type', 'text/html; charset=utf-8');
  res.send(html);
});

export default router;
