package no.nav.registre.testnorge.identservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentAppService {

    private final FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    @SneakyThrows
    public ResponseEntity<String> finnLedigeIdenter(String ident) {

        return filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(ident);
    }
}
