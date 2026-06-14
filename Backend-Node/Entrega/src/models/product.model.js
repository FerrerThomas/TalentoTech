import { db } from '../config/firebase.config.js';
import { collection, getDocs, getDoc, doc, addDoc, deleteDoc } from 'firebase/firestore';

const productsCollection = collection(db, 'products');

export const productModel = {
    getAll: async () => {
        const snapshot = await getDocs(productsCollection);
        return snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
    },
    getById: async (id) => {
        const docRef = doc(db, 'products', id);
        const snapshot = await getDoc(docRef);
        if (snapshot.exists()) {
            return { id: snapshot.id, ...snapshot.data() };
        }
        return null;
    },
    create: async (productData) => {
        const docRef = await addDoc(productsCollection, productData);
        return { id: docRef.id, ...productData };
    },
    delete: async (id) => {
        const docRef = doc(db, 'products', id);
        await deleteDoc(docRef);
        return { id };
    }
};
