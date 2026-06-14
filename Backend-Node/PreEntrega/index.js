const [method, route, title, price, category] = process.argv.slice(2);

const API_URL = 'https://fakestoreapi.com';

async function manageProducts() {
    if (!route) {
        return console.error("Error: Debes proporcionar una 'route' (por ejemplo, 'products' o 'products/1').");
    }

    try {
        if (method === 'GET') {
            const response = await fetch(`${API_URL}/${route}`);
            const data = await response.json();
            console.log(`--- Resultados para GET ${route} ---`);
            console.log(data);
        } 
        else if (method === 'POST') {
            if (!title || !price || !category) {
                return console.error("Error: Para el método POST es obligatorio enviar 'title', 'price' y 'category'.");
            }
            
            const numericPrice = Number(price);
            if (isNaN(numericPrice) || numericPrice <= 0) {
                return console.error("Error: El precio ('price') debe ser un número válido mayor a 0.");
            }

            const config = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: title,
                    price: numericPrice,
                    category: category
                })
            };

            const response = await fetch(`${API_URL}/${route}`, config);
            const data = await response.json();
            console.log(`--- Producto creado ---`);
            console.log(data);
        } 
        else if (method === 'DELETE') {
            const config = {
                method: 'DELETE'
            };

            const response = await fetch(`${API_URL}/${route}`, config);
            const data = await response.json();
            console.log(`--- El item se eliminado con Exito ---`);
            console.log(data);
        } 
        else {
            console.log("Comando no reconocido. Usa GET, POST o DELETE.");
        }

    } catch (error) {
        console.error("Ocurrio un error al procesar la solicitud:", error);
    }
}

manageProducts();
