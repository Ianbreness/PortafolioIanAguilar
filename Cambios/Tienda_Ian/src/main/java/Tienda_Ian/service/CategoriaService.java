package Tienda_Ian.service;

import Tienda_Ian.domain.Categoria;
import Tienda_Ian.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) throws Exception {
        if (imagenFile != null && !imagenFile.isEmpty()) {
            String nombreArchivo = imagenFile.getOriginalFilename();
            categoria.setRutaImagen("/img/" + nombreArchivo);
        }
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void delete(Integer id) throws Exception {
        categoriaRepository.deleteById(id);
    }
}