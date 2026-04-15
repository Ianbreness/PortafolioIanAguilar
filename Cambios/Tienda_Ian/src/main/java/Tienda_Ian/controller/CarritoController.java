package Tienda_Ian.controller;

import Tienda_Ian.domain.Factura;
import Tienda_Ian.domain.Item;
import Tienda_Ian.domain.Usuario;
import Tienda_Ian.service.CarritoService;
import Tienda_Ian.service.FacturaService;
import Tienda_Ian.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;
    private final FacturaService facturaService;

    public CarritoController(CarritoService carritoService,
                             UsuarioService usuarioService,
                             FacturaService facturaService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
        this.facturaService = facturaService;
    }

    // --- 1. MOSTRAR EL CARRITO ---
    @GetMapping("/carrito/listado")
    public String listado(HttpSession session, Model model) {
        List<Item> carrito = carritoService.obtenerCarrito(session);
        BigDecimal total = carritoService.calcularTotal(carrito);
        model.addAttribute("carritoItems", carrito);
        model.addAttribute("carritoTotal", total);
        return "carrito/listado";
    }

    // --- 2. AGREGAR PRODUCTO (vía AJAX) ---
    @PostMapping("/carrito/agregar")
public String agregar(HttpSession session,
                      @RequestParam Integer idProducto,
                      Model model) {

    carritoService.agregarProducto(session, idProducto);

    List<Item> carrito = carritoService.obtenerCarrito(session);
    BigDecimal total = carritoService.calcularTotal(carrito);

    model.addAttribute("items", carrito);
    model.addAttribute("total", total);

    return "carrito/fragmentos :: listadoCarrito";
}

    // --- 3. ELIMINAR PRODUCTO ---
    @PostMapping("/carrito/eliminar")
    public String eliminar(HttpSession session,
                           @RequestParam Integer idProducto,
                           RedirectAttributes redirectAttributes) {
        carritoService.eliminarProducto(session, idProducto);
        redirectAttributes.addFlashAttribute("todoOk", "Producto eliminado del carrito.");
        return "redirect:/carrito/listado";
    }

    // --- 4. MOSTRAR FORMULARIO DE MODIFICAR CANTIDAD ---
    @GetMapping("/carrito/modificar/{idProducto}")
    public String mostrarModificar(@PathVariable Integer idProducto,
                                   HttpSession session,
                                   Model model) {
        List<Item> carrito = carritoService.obtenerCarrito(session);
        Item itemEditar = carrito.stream()
                .filter(i -> i.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
        model.addAttribute("itemEditar", itemEditar);
        return "carrito/modifica";
    }

    // --- 5. GUARDAR NUEVA CANTIDAD ---
    @PostMapping("/carrito/modificar")
    public String guardarModificacion(HttpSession session,
                                      @RequestParam Integer idProducto,
                                      @RequestParam int cantidad,
                                      RedirectAttributes redirectAttributes) {
        carritoService.modificarCantidad(session, idProducto, cantidad);
        redirectAttributes.addFlashAttribute("todoOk", "Cantidad actualizada.");
        return "redirect:/carrito/listado";
    }

    // --- 6. FACTURAR ---
    @PostMapping("/carrito/facturar")
    public String facturar(HttpSession session, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.getUsuarioPorUsername(username).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para facturar.");
            return "redirect:/login";
        }

        Factura factura = carritoService.facturar(session, usuario);
        if (factura == null) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/carrito/listado";
        }

        return "redirect:/carrito/verFactura/" + factura.getIdFactura();
    }

    // --- 7. VER FACTURA ---
    @GetMapping("/carrito/verFactura/{idFactura}")
    public String verFactura(@PathVariable Integer idFactura, Model model) {
        Factura factura = facturaService.getFacturaConVentas(idFactura);
        model.addAttribute("factura", factura);
        return "carrito/verFactura";
    }
}
