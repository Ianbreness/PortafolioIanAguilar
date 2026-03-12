package Tienda_Ian.repository;

import Tienda_Ian.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    public List<Producto> findByActivoTrue();
    public List<Producto> findByCategoriaIdCategoria(Integer idCategoria);
}