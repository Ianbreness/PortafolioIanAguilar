package Tienda_Ian.service;

import Tienda_Ian.domain.Factura;
import Tienda_Ian.repository.FacturaRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @Transactional(readOnly = true) // Solo lectura, mejor rendimiento
    public Factura getFacturaConVentas(Integer idFactura) {
        // 1. Llamar al método del repositorio que usa FETCH JOIN
        // El .orElseThrow lanza una excepción si el Optional está vacío
        return facturaRepository.findByIdFacturaConDetalle(idFactura)
                .orElseThrow(() -> new NoSuchElementException("Factura con ID " + idFactura + " no encontrada."));
    }
}
