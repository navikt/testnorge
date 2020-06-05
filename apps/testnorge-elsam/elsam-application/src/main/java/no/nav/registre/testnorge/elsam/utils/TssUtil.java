package no.nav.registre.testnorge.elsam.utils;

import no.nav.registre.elsam.domain.Lege;
import no.nav.registre.elsam.domain.Lege.LegeBuilder;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.IdentKode;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.Response110;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.Response111;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.TssResponse;

public class TssUtil {

    private TssUtil() {
        throw new IllegalStateException("Utility klasse");
    }

    public static Lege buildLegeFromTssResponse(TssResponse tssResponse) {
        LegeBuilder legeBuilder = Lege.builder();

        if (!tssResponse.getResponse110().isEmpty()) {
            Lege lege = getLegeFrom110(tssResponse.getResponse110().get(0));
            legeBuilder.fnr(lege.getFnr());
            legeBuilder.fornavn(lege.getFornavn());
            legeBuilder.mellomnavn(lege.getMellomnavn());
            legeBuilder.etternavn(lege.getEtternavn());
        }

        for (var response111 : tssResponse.getResponse111()) {
            String hprId = getHprIdFrom111(response111);
            if (hprId != null) {
                legeBuilder.hprId(getHprIdFrom111(response111));
            }
        }

        /*
        for (var response125 : tssResponse.getResponse125()) {
            legeBuilder.legekontor(getLegekontorFrom125(response125));
        }
        */

        return legeBuilder.build();
    }

    private static Lege getLegeFrom110(Response110 response110) {
        Lege lege = Lege.builder().build();
        if (response110.getKodeIdenttype() == IdentKode.FNR) {
            lege.setFnr(response110.getIdOff());
            String[] navn = response110.getNavn().split(" ");
            if (navn.length == 1) {
                lege.setEtternavn(navn[0]);
            } else if (navn.length == 2) {
                lege.setEtternavn(navn[0]);
                lege.setFornavn(navn[1]);
            } else if (navn.length == 3) {
                lege.setEtternavn(navn[0]);
                lege.setFornavn(navn[1]);
                lege.setMellomnavn(navn[2]);
            } else {
                throw new RuntimeException("Kan ikke parse navn på lege. Lengden på navn er ikke mellom 1 og 3.");
            }
        }
        return lege;
    }

    private static String getHprIdFrom111(Response111 response111) {
        if (response111.getKodeAltIdenttype() == IdentKode.HPR) {
            return response111.getIdAlternativ();
        }
        return null;
    }

    // legekontor kan hentes ut fra ereg isteden:
    /*
    private static Legekontor getLegekontorFrom125(Response125 response125) {
        // TODO: Per nå samsvarer ikke kontoret fra TSS det synt forventer å få inn
        // LegekontorTss legekontor = LegekontorTss.builder().eregId(response125.getAvdOffnr())
        //      .avdelingsnavn(response125.getAvdelingsnavn())
        //      .avdelingsnr(response125.getAvdelingsnr())
        //      .typeAvdeling(response125.getTypeAvdeling())
        //      .build();

        Legekontor legekontor = Legekontor.builder()
                .navn("")
                .eregId("")
                .herId("")
                .adresse(Adresse.builder()
                        .gate("")
                        .postnummer("")
                        .by("")
                        .build())
                .build();

        return legekontor;
    }
    */
}
