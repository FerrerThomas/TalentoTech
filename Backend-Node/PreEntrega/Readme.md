# PreEntrega - TalenTech Backend Node

Aplicacion de consola en Node.js para consultar, crear y eliminar productos usando la API de prueba FakeStore

## Requisitos

- Tener instalado Node.js

## Como ejecutarlo

Abrir una terminal en la carpeta del proyecto:

```powershell
cd D:\Facultad\TalentTech\PreEntrega
```

La forma general de ejecutar el programa es:

```powershell
node index.js METODO RUTA
```

Para `POST`, tambien se agregan titulo, precio y categoria:

```powershell
node index.js POST products "Titulo" precio "Categoria"
```

## Como probar los endpoints

### Obtener todos los productos

```powershell
node index.js GET products
```

### Obtener un producto por id

```powershell
node index.js GET products/1
```

### Crear un producto

```powershell
node index.js POST products "Remera prueba" 2500 "ropa"
```

### Eliminar un producto

```powershell
node index.js DELETE products/1
```

