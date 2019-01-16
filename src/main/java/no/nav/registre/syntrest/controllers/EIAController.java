package no.nav.registre.syntrest.controllers;

import no.nav.registre.syntrest.services.EIAService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private Validation validation;

    @Autowired
    private EIAService eiaService;

    @PostMapping(value = "/generateSykemeldinger")
    public ResponseEntity generateSykemeldinger(@RequestBody List<Map<String, String>> request) throws InterruptedException, ExecutionException {
        if (validation.validateEia(request) != true){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }
        CompletableFuture<List<String>> result = eiaService.generateSykemeldingerFromNAIS(request);
        return ResponseEntity.status(HttpStatus.OK).body(result.get());
    }
}
