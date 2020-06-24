package no.nav.registre.populasjoner.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.populasjoner.kafka.person.IdentDetaljDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlDokumentService {

    protected final ObjectMapper mapper;

    @SneakyThrows
    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2))
    public void processBulk(List<DocumentIdWrapper> docs) {
        for (var a : docs) {
            log.info("identifikator: []", a.getIdentifikator());
            if (a.getDokument() != null) {
                List<IdentDetaljDto> identer = a.getDokument().getHentIdenter().getIdenter();
                if (identer != null) {
                    for (var b : identer) {
                        log.info("ident: {}", b.getIdent());
                    }
                }
            }
        }
    }
}