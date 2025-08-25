package no.nav.dolly.bestilling.kontoregisterservice.util;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;

class BankkontoGeneratorTest {

    @Test
    void generateDifferentBankkonto() {
        var kontoer = IntStream.range(1, 100).boxed()
                .map(i -> BankkontoGenerator.tilfeldigUtlandskBankkonto(KontoregisterLandkode.SE.name()))
                .sorted()
                .toList();

        var unikKontoer = kontoer.stream().distinct().count();

        assertThat("forskjellige kontoer", unikKontoer == kontoer.size());
    }

    @Test
    void generateBankkontoWithUknownLandkode() {
        var konto = BankkontoGenerator.tilfeldigUtlandskBankkonto("AABB");
        assertThat("konto har data", !konto.isEmpty());
        assertThat("konto begynner med AA", konto.startsWith("AA"));
    }

    @Test
    void generateBankkontoWithIsoLandkode() {
        var konto = BankkontoGenerator.tilfeldigUtlandskBankkonto("SWE");
        assertThat("konto begynner med SE", konto.startsWith("SE"));
    }

    @Test
    void testTilfeldigNorskkonto() {
        var test = "3654737113";
        /*var digit = */
        BankkontoGenerator.getCheckDigit(test);

        IntStream.range(0, 100).forEach(i -> {
            var norskKonto = BankkontoGenerator.tilfeldigNorskBankkonto();
            var validatingResult = validerNorskKonto(norskKonto);
            assertThat("tilfeldig norsk bankkonto", validatingResult);
        });
    }

    private boolean mod11Kontroll(String kontonummer) {
        var lastDigit = kontonummer.substring(kontonummer.length() - 1);
        var m11 = String.valueOf(
                BankkontoGenerator.getCheckDigit(kontonummer.substring(0, kontonummer.length() - 1))
        ); // exclude kontroll tall
        return lastDigit.equals(m11);
    }

    private boolean validerNorskKonto(String kontonummer) {
        return kontonummer.length() == 11 && !kontonummer.equals("00000000000") && mod11Kontroll(kontonummer);
    }
}
