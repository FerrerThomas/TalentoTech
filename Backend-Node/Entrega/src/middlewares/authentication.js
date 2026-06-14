import jwt from 'jsonwebtoken';
import 'dotenv/config';

const secret_key = process.env.JWT_SECRET_KEY;

// Middleware para verificar el token JWT
export const authentication = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    
    if (!authHeader) {
        return res.status(401).json({ error: "No se proporcionó un token de autorización" });
    }

    const token = authHeader.split(" ")[1];

    if (!token) {
        return res.status(401).json({ error: "Token inválido o mal formateado" });
    }

    jwt.verify(token, secret_key, (err, decoded) => {
        if (err) {
            return res.status(403).json({ error: "Token expirado o inválido (Forbidden)" });
        }
        
        req.user = decoded;
        next();
    });
};
