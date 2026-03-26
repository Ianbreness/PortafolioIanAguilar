/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Tienda_Ian.service;

import com.tienda.domain.Ruta;
import com.tienda.repository.RutaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @Transactional(readOnly=true)
    public List<Ruta> getRutas() {
        return rutaRepository.findAllByOrderByRequiereRolAsc();
    }
}

