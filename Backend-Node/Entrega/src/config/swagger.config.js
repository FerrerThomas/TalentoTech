import swaggerJSDoc from 'swagger-jsdoc';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Ruta absoluta a las rutas asi funciona tanto en local como en Vercel
const routesPath = path.join(__dirname, '../routes/*.js');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'API de Gestión de Productos',
      version: '1.0.0',
      description: 'Documentación de la API de Productos con Autenticación JWT y Firebase',
    },
    servers: [
      {
        url: process.env.API_URL || 'http://localhost:3000',
        description: process.env.API_URL ? 'Servidor en Vercel' : 'Servidor Local',
      },
    ],
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT',
        },
      },
    },
    security: [
      {
        bearerAuth: [],
      },
    ],
  },
  apis: [routesPath], // Ruta absoluta para que funcione en Vercel (serverless)
};

const swaggerSpec = swaggerJSDoc(options);

export default swaggerSpec;
