import { productModel } from '../models/product.model.js';

export const productsService = {
    getAllProducts: async () => {
        return await productModel.getAll();
    },
    getProductById: async (id) => {
        return await productModel.getById(id);
    },
    createProduct: async (productData) => {
        return await productModel.create(productData);
    },
    deleteProduct: async (id) => {
        return await productModel.delete(id);
    }
};
