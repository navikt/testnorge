package no.nav.registre.testnorge.mn.organisasjonapi.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.organisasjonapi.adapter.OrganisasjonAdapter;
import no.nav.registre.testnorge.mn.organisasjonapi.domain.Organisasjon;
import no.nav.registre.testnorge.mn.organisasjonapi.provider.dto.MNOrganisasjonDTO;

@RestController
@RequestMapping("/api/v1/organisasjoner")
@RequiredArgsConstructor
public class OrganisasjonController {

    private final OrganisasjonAdapter orgnaisasjonAdapter;

    @GetMapping
    public ResponseEntity<List<MNOrganisasjonDTO>> getOrganisasjon(
            @RequestHeader("miljo") String miljo,
            @RequestParam(required = false) Boolean active,
            @RequestParam(value = "opplysningspliktig", required = false) Boolean opplysningspliktig
    ) {
        List<MNOrganisasjonDTO> list = orgnaisasjonAdapter
                .getAllBy(active, miljo)
                .stream()
                .map(Organisasjon::toDTO)
                .collect(Collectors.toList());
        if (opplysningspliktig == null) {
            return ResponseEntity.ok(list);
        }
        if (opplysningspliktig) {
            return ResponseEntity.ok(
                    list.stream().filter(MNOrganisasjonDTO::isOpplysningspliktig).collect(Collectors.toList())
            );
        }
        return ResponseEntity.ok(
                list.stream().filter(value -> !value.isOpplysningspliktig()).collect(Collectors.toList())
        );
    }

    @GetMapping("/{orgnummer}")
    public ResponseEntity<MNOrganisasjonDTO> getOrganisasjon(
            @PathVariable("orgnummer") String orgnummer,
            @RequestHeader("miljo") String miljo
    ) {
        Organisasjon organisasjon = orgnaisasjonAdapter.findBy(orgnummer, miljo);
        if (organisasjon == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(organisasjon.toDTO());
    }

    @PutMapping
    public ResponseEntity<?> createOrganisasjon(
            @RequestBody MNOrganisasjonDTO dto,
            @RequestHeader("miljo") String miljo
    ) {
        orgnaisasjonAdapter.save(new Organisasjon(dto), miljo);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orgnummer}")
                .buildAndExpand(dto.getOrgnummer())
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}