package no.nav.registre.testnorge.arena.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.TpsForvalterConsumer;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsForvalterService {

    private static final String BOSATT_STATUS_KODE = "BOSA";

    private final TpsForvalterConsumer tpsForvalterConsumer;

    public boolean identHarPersonstatusBosatt(String ident, String miljoe, LocalDate datoLimit) {
        var status = tpsForvalterConsumer.getPersonstatus(ident, miljoe);

        if (status != null && status.getKodePersonstatus() != null && status.getKodePersonstatus().equals(BOSATT_STATUS_KODE)) {
            return datoLimit == null || (status.getDatoFom() != null && !status.getDatoFom().isAfter(datoLimit));
        } else {
            return false;
        }
    }
}
