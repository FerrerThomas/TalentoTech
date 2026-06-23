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
  apis: ['./src/routes/*.js'], // Se vuelve a la ruta relativa para evitar problemas con las barras invertidas en Windows
};

const swaggerSpec = swaggerJSDoc(options);

export default swaggerSpec;
