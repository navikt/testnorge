package no.nav.registre.spion.domain;

import java.time.LocalDate;
import java.util.Random;

import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;
import no.nav.registre.spion.consumer.rs.response.hodejegeren.HodejegerenResponse;
import org.apache.commons.math3.distribution.GammaDistribution;
import static no.nav.registre.spion.utils.RandomUtils.getRandomBoundedNumber;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Vedtak {

    private final String identitetsnummer;
    private final String fornavn;
    private final String etternavn;
    private final String virksomhetsnummer;
    private final LocalDate fom;
    private final LocalDate tom;
    private final String ytelse = "SP";
    private final String status;
    private final int sykemeldingsgrad;
    private final double refusjonsbeloep;

    public Vedtak(
            HodejegerenResponse persondata,
            AaregResponse arbeidsforhold,
            LocalDate sluttDatoForrigeVedtak,
            boolean isFoersteVedtak){

        LocalDate startDatoPeriode = isFoersteVedtak ? sluttDatoForrigeVedtak: getNextStartDato(sluttDatoForrigeVedtak);
        int periodeLength = getPeriodeLength();
        LocalDate sluttDatoPeriode = startDatoPeriode.plusDays(periodeLength);

        this.identitetsnummer = persondata.getFnr();
        this.fornavn = persondata.getFornavn();
        this.etternavn = persondata.getEtternavn();
        this.virksomhetsnummer = arbeidsforhold.getArbeidsgiver().getOrganisasjonsnummer();
        this.fom = startDatoPeriode;
        this.tom = sluttDatoPeriode;
        this.status = getNyVedtaksstatus();
        this.sykemeldingsgrad = getNySykemeldingsgrad();
        this.refusjonsbeloep = getNyttRefusjonsbeloepForPeriode(periodeLength);

    }

    /**
     * 25% av alle nye perioder må ligge kant-i-kant med en eksisterende periode for å simulere forlengelser.
     * For de resterende trekkes antallet dager mellom perioder fra en uniform fordeling mellom 1 og 21.
     * @param lastSluttDato
     * @return nextStartDato
     */
    private LocalDate getNextStartDato(LocalDate lastSluttDato){
        int numDays = Math.random() < 0.25 ? 0: getRandomBoundedNumber(1,21);
        return lastSluttDato.plusDays(numDays);
    }

    /**
     *Lengden på en periode trekkes fra en gamma-fordeling med shape 1.5 og scale 14.
     * @return periodeLength
     **/
    private int getPeriodeLength(){
        GammaDistribution dist = new GammaDistribution(1.5, 14);
        int result = (int)dist.sample() + 1;

        return result > 248 ? 248 : result;
    }

    /**
     * Status trekkes fra en fordeling slik at vi i snitt får 90% innvilget og 10 % avslått.
     * @return vedtaksstatus
     */
    private String getNyVedtaksstatus(){
        return Math.random() <0.1 ? "AVSLÅTT": "INNVILGET";
    }

    /**
     * Sykmeldingsgrad kan trekkes fra en uniform fordeling mellom 20 og 100. Eventuelt si at 50% av alle sykmeldingsgrader
     * er på 100%, og så fordele uniformt mellom 20% og 90%.
     * @return sykemeldingsgrad
     */
    private int getNySykemeldingsgrad(){
        return Math.random()<0.5 ? 100 : getRandomBoundedNumber(20, 90);
    }

    /**
     * Refusjonsbeløpet kan trekkes fra en uniform fordeling mellom 200 og 2400 og så gange opp
     * med antallet dager i perioden (dette blir ikke helt riktig da vi også kan ta med ikke-virkedager).
     * @param periodeLength
     * @return refusjonsbeløp for perioden
     */
    private int getNyttRefusjonsbeloepForPeriode(int periodeLength){
        return getRandomBoundedNumber(200, 2400)*periodeLength;
    }

}
