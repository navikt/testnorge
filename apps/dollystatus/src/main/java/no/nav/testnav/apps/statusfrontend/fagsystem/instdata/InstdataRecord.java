package no.nav.testnav.apps.statusfrontend.fagsystem.instdata;

import java.time.LocalDate;

public record InstdataRecord(
        String norskident,
        String tssEksternId,
        String institusjonstype,
        String oppholdstype,
        LocalDate startdato,
        LocalDate sluttdato,
        LocalDate forventetSluttdato,
        String registrertAv
) {
}
