package it.uniroma3.siw.torneocalcio.controller.api;

import it.uniroma3.siw.torneocalcio.service.PartitaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/*
netstat -ano | find "8080"
tasklist /fi "PID eq [PID]"
taskkill /F /PID [PID]
*/


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

    @Autowired PartitaService partitaService;

    @GetMapping("/tornei/{torneoId}/classifica")
    public ResponseEntity<List<PartitaService.ClassificaEntry>> getClassifica(
            @PathVariable Long torneoId) {
        List<PartitaService.ClassificaEntry> classifica =
                partitaService.calcolaClassifica(torneoId);
        return ResponseEntity.ok(classifica);
    }
}
