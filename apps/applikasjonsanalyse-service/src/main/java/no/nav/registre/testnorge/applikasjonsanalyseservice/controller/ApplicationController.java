package no.nav.registre.testnorge.applikasjonsanalyseservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    @PutMapping
    public ResponseEntity<HttpStatus> save(@RequestBody ApplicationInfoDTO dto){
        return ResponseEntity.ok();
    }

}
