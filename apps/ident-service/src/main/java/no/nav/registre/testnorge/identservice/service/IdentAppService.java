package no.nav.registre.testnorge.identservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentAppService {

    private final FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    @SneakyThrows
    public ResponseEntity<List<String>> finnLedigeIdenter(List<String> identer) {

        return filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(identer);
    }
}
