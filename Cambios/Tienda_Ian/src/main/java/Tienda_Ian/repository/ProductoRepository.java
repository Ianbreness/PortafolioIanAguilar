package Tienda_Ian.repository;

import Tienda_Ian.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIdCategoria(Integer idCategoria);

    // Consulta derivada
    List<Producto> findByPrecioBetween(BigDecimal precioInf, BigDecimal precioSup);

    // Consulta JPQL
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup")
    List<Producto> buscarPorRangoPrecioJPQL(
            @Param("precioInf") BigDecimal precioInf,
            @Param("precioSup") BigDecimal precioSup);

    // Consulta SQL nativa
    @Query(value = "SELECT * FROM producto WHERE precio BETWEEN :precioInf AND :precioSup",
           nativeQuery = true)
    List<Producto> buscarPorRangoPrecioSQL(
            @Param("precioInf") BigDecimal precioInf,
            @Param("precioSup") BigDecimal precioSup);
}