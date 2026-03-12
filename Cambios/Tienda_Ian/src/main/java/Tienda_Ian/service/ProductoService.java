package Tienda_Ian.service;

import Tienda_Ian.domain.Producto;
import Tienda_Ian.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto getProducto(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) throws Exception {
        if (imagenFile != null && !imagenFile.isEmpty()) {
            String nombreArchivo = imagenFile.getOriginalFilename();
            producto.setRutaImagen("/img/" + nombreArchivo);
        }
        productoRepository.save(producto);
    }

    @Transactional
    public void delete(Integer id) throws Exception {
        productoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaDerivada(double precioInf, double precioSup) {
        return productoRepository.findByPrecioBetween(
                new BigDecimal(precioInf),
                new BigDecimal(precioSup));
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaJPQL(double precioInf, double precioSup) {
        return productoRepository.buscarPorRangoPrecioJPQL(
                new BigDecimal(precioInf),
                new BigDecimal(precioSup));
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaSQL(double precioInf, double precioSup) {
        return productoRepository.buscarPorRangoPrecioSQL(
                new BigDecimal(precioInf),
                new BigDecimal(precioSup));
    }
}