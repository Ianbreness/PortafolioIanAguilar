package Tienda_Ian.repository;

import Tienda_Ian.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameAndActivoTrue(String username);
}