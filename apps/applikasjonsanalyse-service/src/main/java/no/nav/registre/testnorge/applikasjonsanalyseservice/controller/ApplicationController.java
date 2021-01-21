package no.nav.registre.testnorge.applikasjonsanalyseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import no.nav.registre.testnorge.applikasjonsanalyseservice.adapter.ApplicationAdapter;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.ApplicationInfo;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationAdapter applicationAdapter;

    @PutMapping
    public ResponseEntity<HttpStatus> save(@RequestBody ApplicationInfoDTO dto){
        applicationAdapter.save(new ApplicationInfo(dto));
        return ResponseEntity.ok().build();
    }

}
