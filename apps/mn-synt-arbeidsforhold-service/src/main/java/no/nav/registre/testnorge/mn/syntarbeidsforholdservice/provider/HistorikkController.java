package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter.ArbeidsforholdHistorikkAdapter;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.ArbeidsforholdHistorikk;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/arbeidsforhold/historikk")
public class HistorikkController {
    private final ArbeidsforholdHistorikkAdapter adapter;

    @GetMapping
    public ResponseEntity<List<ArbeidsforholdHistorikk>> get(@RequestHeader("miljo") String miljo) {
        return ResponseEntity.ok(adapter.getAllFrom(miljo));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@RequestHeader("miljo") String miljo) {
        adapter.deleteBy(miljo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{arbeidsforholdId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("arbeidsforholdId") String arbeidsforholdId, @RequestHeader("miljo") String miljo) {
        adapter.deleteBy(arbeidsforholdId, miljo);
        return ResponseEntity.noContent().build();
    }
}
