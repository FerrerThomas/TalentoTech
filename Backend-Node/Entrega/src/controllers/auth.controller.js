import { generateToken } from '../utils/token-generator.js';

const default_user = {
    id: 1,
    email: "user@email.com",
    password: "strongPass123"
};

export async function login(req, res) {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ error: "Faltan credenciales (email o password)" });
        }

        if (email === default_user.email && password === default_user.password) {
            const user = { id: default_user.id, email };
            const token = generateToken(user);
            
            return res.json({ token });
        } else {
            return res.status(401).json({ error: "Credenciales inválidas" });
        }
    } catch (error) {
        return res.status(500).json({ error: "Ocurrió un error en el servidor" });
    }
}
