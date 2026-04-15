package Tienda_Ian.service;

import Tienda_Ian.domain.*;
import Tienda_Ian.repository.FacturaRepository;
import Tienda_Ian.repository.ProductoRepository;
import Tienda_Ian.repository.VentaRepository;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private static final String ATTRIBUTE_CARRITO = "carrito";

    private final ProductoRepository productoRepository;
    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;

    public CarritoService(ProductoRepository productoRepository,
                          FacturaRepository facturaRepository,
                          VentaRepository ventaRepository) {
        this.productoRepository = productoRepository;
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
    }

    // --- 1. Gestión de Sesión ---
    @SuppressWarnings("unchecked")
    public List<Item> obtenerCarrito(HttpSession session) {
        List<Item> carrito = (List<Item>) session.getAttribute(ATTRIBUTE_CARRITO);
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(ATTRIBUTE_CARRITO, carrito);
        }
        return carrito;
    }

    public BigDecimal calcularTotal(List<Item> carrito) {
        return carrito.stream()
                .map(Item::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- 2. Agregar producto al carrito (cantidad = 1 por defecto) ---
    public void agregarProducto(HttpSession session, Integer idProducto) {
        List<Item> carrito = obtenerCarrito(session);
        Optional<Producto> optProducto = productoRepository.findById(idProducto);
        if (optProducto.isEmpty()) return;

        Producto producto = optProducto.get();

        // Si ya existe en el carrito, incrementa cantidad
        for (Item item : carrito) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                item.setCantidad(item.getCantidad() + 1);
                session.setAttribute(ATTRIBUTE_CARRITO, carrito);
                return;
            }
        }

        // Si no existe, agrega nuevo Item
        Item nuevoItem = new Item(producto, 1, producto.getPrecio());
        carrito.add(nuevoItem);
        session.setAttribute(ATTRIBUTE_CARRITO, carrito);
    }

    // --- 3. Eliminar producto ---
    public void eliminarProducto(HttpSession session, Integer idProducto) {
        List<Item> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getProducto().getIdProducto().equals(idProducto));
        session.setAttribute(ATTRIBUTE_CARRITO, carrito);
    }

    // --- 4. Modificar cantidad ---
    public void modificarCantidad(HttpSession session, Integer idProducto, int cantidad) {
        List<Item> carrito = obtenerCarrito(session);
        for (Item item : carrito) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                if (cantidad <= 0) {
                    carrito.remove(item);
                } else {
                    item.setCantidad(cantidad);
                }
                break;
            }
        }
        session.setAttribute(ATTRIBUTE_CARRITO, carrito);
    }

    // --- 5. Facturar (convierte el carrito en Factura + Ventas en BD) ---
    @Transactional
    public Factura facturar(HttpSession session, Usuario usuario) {
        List<Item> carrito = obtenerCarrito(session);
        if (carrito.isEmpty()) return null;

        // Crear la factura
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setEstado(EstadoFactura.Pagada);
        factura.setTotal(calcularTotal(carrito));
        factura = facturaRepository.save(factura);

        // Crear una venta por cada ítem
        for (Item item : carrito) {
            Venta venta = new Venta();
            venta.setFactura(factura);
            venta.setProducto(item.getProducto());
            venta.setCantidad(item.getCantidad());
            venta.setPrecioHistorico(item.getProducto().getPrecio());
            ventaRepository.save(venta);
        }

        // Limpiar el carrito de la sesión
        session.removeAttribute(ATTRIBUTE_CARRITO);

        return factura;
    }
}
