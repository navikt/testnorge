package no.nav.dolly.mapper.strategy;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukerUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger.NyeDagp;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap115;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaDagpenger;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.UTEN_SERVICEBEHOV;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger.DAGPENGER_VILKAAR;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe.IKVAL;

@Slf4j
@Component
public class ArenaMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Arenadata.class, ArenaNyBruker.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Arenadata arenadata, ArenaNyBruker arenaNyBruker, MappingContext context) {

                        if (UTEN_SERVICEBEHOV.equals(arenadata.getArenaBrukertype())) {
                            arenaNyBruker.setUtenServicebehov(new ArenaBrukerUtenServicebehov());

                            arenaNyBruker.setKvalifiseringsgruppe(IKVAL);
                            if (nonNull(arenadata.getInaktiveringDato())) {
                                arenaNyBruker.getUtenServicebehov().setStansDato(arenadata.getInaktiveringDato().toLocalDate());
                            }
                        } else if (!arenadata.getAap().isEmpty() || !arenadata.getAap115().isEmpty() || !arenadata.getDagpenger().isEmpty()) {
                            arenaNyBruker.setAktiveringsDato(
                                    Stream.of(
                                            arenadata.getAap().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(RsArenaAap::getFraDato),
                                            arenadata.getAap115().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(RsArenaAap115::getFraDato),
                                            arenadata.getDagpenger().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(RsArenaDagpenger::getFraDato))
                                            .flatMap(Stream::distinct)
                                            .map(LocalDateTime::toLocalDate)
                                            .min(LocalDate::compareTo)
                                            .orElse(null));
                        }
                        if (arenadata.getAap().isEmpty()) {
                            arenaNyBruker.setAap(null);
                        }
                        if (arenadata.getAap115().isEmpty()) {
                            arenaNyBruker.setAap115(null);
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Arenadata.class, ArenaDagpenger.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Arenadata arenadata, ArenaDagpenger arenaDagpenger, MappingContext context) {
                        RsArenaDagpenger rsArenaDagpenger = arenadata.getDagpenger().get(0);

                        NyeDagp dagpenger = new NyeDagp();

                        dagpenger.setVedtaksperiode(ArenaDagpenger.Vedtaksperiode.builder()
                                .fom(rsArenaDagpenger.getFraDato().toLocalDate())
                                .build());

                        dagpenger.setRettighetKode(rsArenaDagpenger.getRettighetKode());

                        dagpenger.setDagpengeperiode(ArenaDagpenger.Dagpengeperiode.builder()
                                .nullstillPeriodeteller("J")
                                .nullstillPermitteringsteller("N")
                                .nullstillPermitteringstellerFisk("N")
                                .build());

                        dagpenger.setGodkjenningerReellArbeidssoker(ArenaDagpenger.GodkjenningerReellArbeidssoker.builder()
                                .godkjentDeltidssoker("N")
                                .godkjentLokalArbeidssoker("N")
                                .godkjentUtdanning("J")
                                .build());

                        dagpenger.setTaptArbeidstid(ArenaDagpenger.TaptArbeidstid.builder()
                                .anvedtRegelKode("GJSNITT12MND")
                                .fastsattArbeidstid(30)
                                .naavaerendeArbeidstid(0)
                                .build());

                        dagpenger.setUtfall("JA");

                        dagpenger.setVedtaktype("O");

                        dagpenger.setVilkaar(DAGPENGER_VILKAAR);

                        if (nonNull(rsArenaDagpenger.getTilDato())) {
                            dagpenger.getVedtaksperiode().setTom(rsArenaDagpenger.getTilDato().toLocalDate());
                        }
                        dagpenger.setDatoMottatt(nonNull(rsArenaDagpenger.getMottattDato())
                                ? rsArenaDagpenger.getMottattDato().toLocalDate()
                                : rsArenaDagpenger.getFraDato().toLocalDate());

                        arenaDagpenger.setNyeDagp(List.of(dagpenger));
                    }
                })
                .byDefault()
                .register();
    }
}
