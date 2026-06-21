import express from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';
import 'dotenv/config';

// Importar Swagger
import swaggerUi from 'swagger-ui-express';
import swaggerSpec from './src/config/swagger.config.js';

// Importar rutas
import authRoutes from './src/routes/auth.routes.js';
import productsRoutes from './src/routes/products.routes.js';

// Importar middleware 404
import { notFoundHandler } from './src/middlewares/404.middleware.js';

const app = express();
const PORT = process.env.PORT || 3000;

// Configurar middlewares globales
app.use(cors());
app.use(bodyParser.json());

// Configurar rutas
app.use('/auth', authRoutes);
app.use('/api/products', productsRoutes);

// Configurar Swagger
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

// Configurar middleware para rutas desconocidas (404)
app.use(notFoundHandler);

// Iniciar servidor localmente solo si no estamos en Vercel
if (process.env.NODE_ENV !== 'production') {
    app.listen(PORT, () => {
        console.log(`Servidor corriendo en http://localhost:${PORT}`);
    });
}

export default app;
