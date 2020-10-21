package no.nav.registre.testnorge.arbeidsforhold.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.arbeidsforhold.domain.Opplysningspliktig;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;

@RestController
@RequestMapping("/api/v1/opplysningspliktig")
@RequiredArgsConstructor
public class OpplysningspliktigController {

    @PostMapping
    public ResponseEntity<HttpStatus> createArbeidsforhold(@RequestBody OpplysningspliktigDTO opplysningspliktigDTO) {
        Opplysningspliktig opplysningspliktig = new Opplysningspliktig(opplysningspliktigDTO);
        // TODO Send inn opplysningspliktig til aareg
        return ResponseEntity.ok().build();
    }
}