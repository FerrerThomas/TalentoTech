export const notFoundHandler = (req, res, next) => {
    res.status(404).json({
        error: "Ruta no encontrada",
        message: `La ruta ${req.originalUrl} no existe en esta API.`
    });
};
