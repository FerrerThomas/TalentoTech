import swaggerJSDoc from 'swagger-jsdoc';
import path from 'path';

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
        url: 'http://localhost:3000',
        description: 'Servidor Local',
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
  apis: [path.join(process.cwd(), 'src/routes/*.js')], // Ruta absoluta para Vercel
};

const swaggerSpec = swaggerJSDoc(options);

export default swaggerSpec;
