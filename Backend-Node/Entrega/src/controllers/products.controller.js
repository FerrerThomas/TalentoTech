import { productsService } from '../services/products.service.js';

export const getAllProducts = async (req, res) => {
    try {
        const products = await productsService.getAllProducts();
        res.json(products);
    } catch (error) {
        res.status(500).json({ error: "Error al obtener los productos" });
    }
};

export const getProductById = async (req, res) => {
    try {
        const { id } = req.params;
        const product = await productsService.getProductById(id);
        
        if (!product) {
            return res.status(404).json({ error: "Producto no encontrado" });
        }
        res.json(product);
    } catch (error) {
        res.status(500).json({ error: "Error al obtener el producto" });
    }
};

export const createProduct = async (req, res) => {
    try {
        const { title, price, category } = req.body;
        
        if (!title || !price || !category) {
            return res.status(400).json({ error: "Faltan datos requeridos (title, price, category)" });
        }

        const newProduct = await productsService.createProduct({ title, price: Number(price), category });
        res.status(201).json(newProduct);
    } catch (error) {
        res.status(500).json({ error: "Error al crear el producto" });
    }
};

export const deleteProduct = async (req, res) => {
    try {
        const { id } = req.params;
        
        // Verificamos si existe antes de eliminar
        const product = await productsService.getProductById(id);
        if (!product) {
            return res.status(404).json({ error: "Producto no encontrado para eliminar" });
        }

        const result = await productsService.deleteProduct(id);
        res.json({ message: "Producto eliminado correctamente", result });
    } catch (error) {
        res.status(500).json({ error: "Error al eliminar el producto" });
    }
};
