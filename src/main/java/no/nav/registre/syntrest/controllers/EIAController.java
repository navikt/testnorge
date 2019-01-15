package no.nav.registre.syntrest.controllers;

import no.nav.registre.syntrest.services.EIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1")
public class EIAController {
    @Autowired
    private EIAService eiaService;

    @PostMapping(value = "/generateEia")
    public List<String> generateSykemeldinger(@RequestBody List<Map<String, String>> request) throws InterruptedException, ExecutionException {
        CompletableFuture<List<String>> result = eiaService.generateSykemeldingerFromNAIS(request);
        return result.get();
    }
}
