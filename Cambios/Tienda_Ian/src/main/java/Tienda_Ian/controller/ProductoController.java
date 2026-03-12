package Tienda_Ian.controller;
import Tienda_Ian.domain.Producto;
import Tienda_Ian.service.CategoriaService;
import Tienda_Ian.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "/producto/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.getProducto(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "/producto/modifica"; // ← único cambio
    }

    @PostMapping("/guardar")
    public String guardar(Producto producto,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            Model model) {
        try {
            productoService.save(producto, imagenFile);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(Integer idProducto, Model model) {
        try {
            productoService.delete(idProducto);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/producto/listado";
    }
}