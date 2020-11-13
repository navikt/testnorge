package no.nav.registre.sdforvalter.provider.rs.v1;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.consumer.rs.EregMapperConsumer;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.status.ereg.OrganisasjonStatusMap;
import no.nav.registre.sdforvalter.service.EregStatusService;
import no.nav.registre.testnorge.libs.dto.statiskedataforvalter.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.dto.statiskedataforvalter.v1.OrganisasjonListeDTO;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjons")
public class OrganisasjonControllerV1 {

    private static final String ORGNR_REGEX = "^(8|9)\\d{8}$";

    private final EregMapperConsumer eregMapperConsumer;
    private final EregAdapter eregAdapter;
    private final EregStatusService eregStatusService;

    @GetMapping(value = "/flatfile", produces = "text/ISO-8859-1")
    public void exportByGruppe(
            @RequestParam(name = "gruppe") String gruppe,
            @RequestParam(name = "update") Boolean update,
            HttpServletResponse response
    ) throws IOException {
        writeExport(response, eregAdapter.fetchBy(gruppe), gruppe, update);
    }

    @GetMapping(value = "/flatfile/{orgnr}", produces = "text/ISO-8859-1")
    public void exportByOrgnr(
            @PathParam("orgnr") String orgnr,
            @RequestParam(name = "update") Boolean update,
            HttpServletResponse response
    ) throws IOException {
        writeExport(response, eregAdapter.fetchByIds(Set.of(orgnr)), orgnr, update);
    }

    @GetMapping("/status")
    public ResponseEntity<OrganisasjonStatusMap> statusByGruppe(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe", required = false) String gruppe
    ) {
        return ResponseEntity.ok(eregStatusService.getStatusByGruppe(miljo, gruppe, equal));
    }

    @Validated
    @GetMapping("/status/{orgnr}")
    public ResponseEntity<OrganisasjonStatusMap> statusByOrgnr(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @PathParam("orgnr") @Pattern(regexp = ORGNR_REGEX) String orgnr
    ) {
        return ResponseEntity.ok(eregStatusService.getStatusByOrgnr(miljo, orgnr, equal));
    }

    @GetMapping
    public ResponseEntity<OrganisasjonListeDTO> getOrganisasjons(@RequestParam(name = "gruppe", required = false) String gruppe) {
        OrganisasjonListeDTO dto = eregAdapter.fetchBy(gruppe).toDTO();
        return ResponseEntity.ok(dto);
    }

    @Validated
    @GetMapping("/{orgnr}")
    public ResponseEntity<OrganisasjonDTO> getOrganisasjon(@PathParam("orgnr") @Pattern(regexp = ORGNR_REGEX) String orgnr) {
        Ereg ereg = eregAdapter.fetchByOrgnr(orgnr);
        if (ereg == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ereg.toDTO());
    }

    @Validated
    @PutMapping
    public ResponseEntity<OrganisasjonListeDTO> createOrganisasjons(@RequestBody OrganisasjonListeDTO listeDTO) {
        OrganisasjonListeDTO dto = eregAdapter.save(new EregListe(listeDTO)).toDTO();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/flatfil")
    public ResponseEntity<OrganisasjonListeDTO> createEregStaticDataFromFlatfil(@RequestBody String flatfil) {
        EregListe eregListe = eregMapperConsumer.flatfilToJSON(flatfil);
        eregAdapter.save(eregListe);
        return ResponseEntity.ok(eregListe.toDTO());
    }

    private void writeExport(HttpServletResponse response, EregListe liste, String identifier, Boolean update) throws IOException {
        response.setContentType("text/" + StandardCharsets.ISO_8859_1);
        String filename = "filename=ereg-" + Strings.nullToEmpty(identifier) + "-" + LocalDateTime.now().toString() + ".txt";
        response.setHeader("Content-Disposition", "attachment; " + filename);
        String flatfil = eregMapperConsumer.generateFlatfil(liste, update);
        response.getOutputStream().write(flatfil.getBytes(StandardCharsets.ISO_8859_1));
    }
}