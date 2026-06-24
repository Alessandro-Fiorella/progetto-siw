package it.uniroma3.siw.torneocalcio.controller.api;

import it.uniroma3.siw.torneocalcio.service.PartitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller che espone i dati della classifica in formato JSON.
 * Consumato dal componente React nella pagina classifica/classifica.html.
 *
 * Endpoint: GET /api/tornei/{torneoId}/classifica
 * Risposta: array di ClassificaEntry serializzato come JSON
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClassificaApiController {

    private final PartitaService partitaService;

    @GetMapping("/tornei/{torneoId}/classifica")
    public ResponseEntity<List<PartitaService.ClassificaEntry>> getClassifica(
            @PathVariable Long torneoId) {
        List<PartitaService.ClassificaEntry> classifica =
                partitaService.calcolaClassifica(torneoId);
        return ResponseEntity.ok(classifica);
    }
}
