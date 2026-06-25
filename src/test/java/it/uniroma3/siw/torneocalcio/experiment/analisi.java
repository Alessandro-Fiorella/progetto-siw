package it.uniroma3.siw.torneocalcio.experiment;

import it.uniroma3.siw.torneocalcio.model.Torneo;
import it.uniroma3.siw.torneocalcio.repository.TorneoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class analisi{

    @Autowired private TorneoRepository torneoRepository;
    @Autowired private EntityManager entityManager;

    @Test
    @Transactional 
    public void eseguiSperimentazione() {

        System.out.println("=== INIZIO ANALISI SPERIMENTALE ===");

        // ---------------------------------------------------------
        // STRATEGIA 1: LAZY FETCHING (Il problema N+1)
        // ---------------------------------------------------------
        long startLazy = System.currentTimeMillis();
        
        // 1 Query per prendere tutti i tornei
        List<Torneo> torneiLazy = torneoRepository.findAll(); 
        
        // N Query aggiuntive (una per ogni torneo) per prendere le squadre
        for (Torneo t : torneiLazy) {
            t.getSquadre().size(); // Triggera il caricamento LAZY
        }
        
        long endLazy = System.currentTimeMillis();
        System.out.println("1. Tempo LAZY Fetch (N+1 query): " + (endLazy - startLazy) + " ms");

        // Svuotiamo la cache di primo livello di Hibernate per non falsare i test successivi
        entityManager.clear();

        // ---------------------------------------------------------
        // STRATEGIA 2: JOIN FETCH
        // ---------------------------------------------------------
        long startJoinFetch = System.currentTimeMillis();
        
        // 1 Singola Query SQL con JOIN
        List<Torneo> torneiJoinFetch = torneoRepository.findAllWithSquadre(); 
        
        for (Torneo t : torneiJoinFetch) {
            t.getSquadre().size(); // I dati sono già in RAM, non fa query al DB!
        }
        
        long endJoinFetch = System.currentTimeMillis();
        System.out.println("2. Tempo JOIN FETCH (1 singola query): " + (endJoinFetch - startJoinFetch) + " ms");

        entityManager.clear();

        // ---------------------------------------------------------
        // STRATEGIA 3: ENTITY GRAPH
        // ---------------------------------------------------------
        long startEntityGraph = System.currentTimeMillis();
        
        // 1 Singola Query SQL generata dinamicamente
        List<Torneo> torneiGraph = torneoRepository.findAllWithEntityGraph();
        
        for (Torneo t : torneiGraph) {
            t.getSquadre().size();
        }
        
        long endEntityGraph = System.currentTimeMillis();
        System.out.println("3. Tempo ENTITY GRAPH (1 singola query): " + (endEntityGraph - startEntityGraph) + " ms");

        System.out.println("=== FINE ANALISI SPERIMENTALE ===");
    }
}