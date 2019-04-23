package no.nav.registre.aaregstub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.arbeidsforhold.Ident;
import no.nav.registre.aaregstub.arbeidsforhold.consumer.rs.DollyConsumer;
import no.nav.registre.aaregstub.arbeidsforhold.consumer.rs.responses.DollyResponse;
import no.nav.registre.aaregstub.arbeidsforhold.contents.AntallTimerForTimeloennet;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsavtale;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Permisjon;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Utenlandsopphold;
import no.nav.registre.aaregstub.provider.rs.responses.StatusResponse;
import no.nav.registre.aaregstub.repository.AntallTimerForTimeloennetRepository;
import no.nav.registre.aaregstub.repository.ArbeidsavtaleRepository;
import no.nav.registre.aaregstub.repository.ArbeidsforholdRepository;
import no.nav.registre.aaregstub.repository.IdentRepository;
import no.nav.registre.aaregstub.repository.PermisjonRepository;
import no.nav.registre.aaregstub.repository.UtenlandsoppholdRepository;

@Service
@Slf4j
public class ArbeidsforholdService {

    @Autowired
    private IdentRepository identRepository;

    @Autowired
    private ArbeidsforholdRepository arbeidsforholdRepository;

    @Autowired
    private ArbeidsavtaleRepository arbeidsavtaleRepository;

    @Autowired
    private AntallTimerForTimeloennetRepository antallTimerForTimeloennetRepository;

    @Autowired
    private PermisjonRepository permisjonRepository;

    @Autowired
    private UtenlandsoppholdRepository utenlandsoppholdRepository;

    @Autowired
    private DollyConsumer dollyConsumer;

    public StatusResponse lagreArbeidsforhold(List<ArbeidsforholdsResponse> arbeidsforholdsmeldinger, Boolean lagreIAareg) {
        behandleNyttArbeidsforhold(arbeidsforholdsmeldinger);
        List<String> identerLagretIStub = new ArrayList<>(arbeidsforholdsmeldinger.size());
        List<String> identerLagretIAareg = new ArrayList<>();
        Map<String, String> identerIkkeLagretIAareg = new HashMap<>();
        Map<String, Object> tokenObject = null;

        if (lagreIAareg) {
            tokenObject = hentTokenTilDolly();
        }

        for (ArbeidsforholdsResponse arbeidsforholdsResponse : arbeidsforholdsmeldinger) {
            String ident = arbeidsforholdsResponse.getArbeidsforhold().getArbeidstaker().getIdent();
            identerLagretIStub.add(ident);

            if (lagreIAareg) {
                DollyResponse dollyResponse = dollyConsumer.sendArbeidsforholdTilAareg(tokenObject, arbeidsforholdsResponse);
                if (HttpStatus.CREATED.equals(dollyResponse.getHttpStatus())) {
                    for (String miljoe : arbeidsforholdsResponse.getEnvironments()) {
                        if ("OK".equals(dollyResponse.getStatusPerMiljoe().get(miljoe))) {
                            identerLagretIAareg.add(ident);
                        } else {
                            identerIkkeLagretIAareg.put(ident, dollyResponse.getStatusPerMiljoe().get(miljoe));
                        }
                    }
                }
            }
        }

        return StatusResponse.builder()
                .identerLagretIStub(sjekkOmIdenterErLagret(identerLagretIStub))
                .identerLagretIAareg(identerLagretIAareg)
                .identerSomIkkeKunneLagresIAareg(identerIkkeLagretIAareg)
                .build();
    }

    public Optional<Ident> hentIdentMedArbeidsforholdNy(String ident) {
        return identRepository.findByFnr(ident);
    }

    public List<BigInteger> hentAlleArbeidsforholdIder() {
        return arbeidsforholdRepository.getAllIds();
    }

    public Optional<Arbeidsforhold> hentArbeidsforhold(Long id) {
        return arbeidsforholdRepository.findById(id);
    }

    public void slettArbeidsforhold(Long id) {
        Arbeidsforhold arbeidsforhold = arbeidsforholdRepository.findById(id).orElse(null);

        if (arbeidsforhold != null) {
            String fnr = arbeidsforhold.getArbeidstaker().getIdent();
            arbeidsforhold.setIdenten(null);

            Long arbeidsavtaleId = arbeidsforhold.getArbeidsavtale().getId();
            arbeidsforhold.setArbeidsavtale(null);

            List<AntallTimerForTimeloennet> antallTimerForTimeloennet = arbeidsforhold.getAntallTimerForTimeloennet();
            if (antallTimerForTimeloennet != null) {
                for (AntallTimerForTimeloennet antallTimer : antallTimerForTimeloennet) {
                    Long antallTimerForTimeloennetId = antallTimer.getId();
                    antallTimer.setArbeidsforholdet(null);
                    antallTimerForTimeloennetRepository.deleteById(antallTimerForTimeloennetId);
                }
            }

            List<Permisjon> permisjoner = arbeidsforhold.getPermisjon();
            if (permisjoner != null) {
                for (Permisjon permisjon : permisjoner) {
                    Long permisjonId = permisjon.getId();
                    permisjon.setArbeidsforholdet(null);
                    permisjonRepository.deleteById(permisjonId);
                }
            }

            List<Utenlandsopphold> utenlandsopphold = arbeidsforhold.getUtenlandsopphold();
            if (utenlandsopphold != null) {
                for (Utenlandsopphold opphold : utenlandsopphold) {
                    Long utenlandsoppholdId = opphold.getId();
                    opphold.setArbeidsforholdet(null);
                    utenlandsoppholdRepository.deleteById(utenlandsoppholdId);
                }
            }

            arbeidsavtaleRepository.deleteById(arbeidsavtaleId);

            Ident ident = identRepository.findByFnr(fnr).orElse(null);

            if (ident != null) {
                ident.getArbeidsforhold().remove(arbeidsforhold);
                if (ident.getArbeidsforhold().isEmpty()) {
                    identRepository.deleteById(fnr);
                }
            }

            arbeidsforholdRepository.deleteById(id);
        }
    }

    public List<String> hentAlleArbeidstakere() {
        return identRepository.getAllDistinctIdents();
    }

    private void behandleNyttArbeidsforhold(List<ArbeidsforholdsResponse> arbeidsforholdsmeldinger) {
        for (ArbeidsforholdsResponse arbeidsforholdsResponse : arbeidsforholdsmeldinger) {
            Arbeidsforhold arbeidsforhold = arbeidsforholdsResponse.getArbeidsforhold();
            String nyIdent = arbeidsforhold.getArbeidstaker().getIdent();

            Ident ident = identRepository.findByFnr(nyIdent).orElse(null);

            Arbeidsavtale arbeidsavtale = arbeidsforhold.getArbeidsavtale();
            arbeidsavtale.setArbeidsforholdet(arbeidsforhold);

            List<AntallTimerForTimeloennet> antallTimerForTimeloennet = arbeidsforhold.getAntallTimerForTimeloennet();
            if (antallTimerForTimeloennet != null) {
                for (AntallTimerForTimeloennet antallTimer : antallTimerForTimeloennet) {
                    antallTimer.setArbeidsforholdet(arbeidsforhold);
                }
            }

            List<Permisjon> permisjoner = arbeidsforhold.getPermisjon();
            if (permisjoner != null) {
                for (Permisjon permisjon : permisjoner) {
                    permisjon.setArbeidsforholdet(arbeidsforhold);
                }
            }

            List<Utenlandsopphold> utenlandsopphold = arbeidsforhold.getUtenlandsopphold();
            if (utenlandsopphold != null) {
                for (Utenlandsopphold opphold : utenlandsopphold) {
                    opphold.setArbeidsforholdet(arbeidsforhold);
                }
            }

            if (ident != null) {
                ident.getArbeidsforhold().add(arbeidsforhold);
            } else {
                ident = Ident.builder().fnr(nyIdent).arbeidsforhold(new ArrayList<>(Arrays.asList(arbeidsforhold))).build();
            }

            arbeidsforhold.setIdenten(ident);

            identRepository.save(ident);
        }
    }

    public List<DollyResponse> sendArbeidsforholdTilAareg(List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold) {
        List<DollyResponse> responses = new ArrayList<>(syntetiserteArbeidsforhold.size());
        Map<String, Object> tokenObject = hentTokenTilDolly();
        for (ArbeidsforholdsResponse syntetisertArbeidsforhold : syntetiserteArbeidsforhold) {
            responses.add(dollyConsumer.sendArbeidsforholdTilAareg(tokenObject, syntetisertArbeidsforhold));
        }
        return responses;
    }

    public Object hentArbeidsforholdFraAareg(String ident, String miljoe) {
        Map<String, Object> tokenObject = hentTokenTilDolly();
        return dollyConsumer.hentArbeidsforholdFraAareg(tokenObject, ident, miljoe);
    }

    private List<String> sjekkOmIdenterErLagret(List<String> identer) {
        List<String> lagredeIdenter = new ArrayList<>();
        for (String ident : identer) {
            if (identRepository.findByFnr(ident).isPresent()) {
                lagredeIdenter.add(ident);
            }
        }
        return lagredeIdenter;
    }

    private Map<String, Object> hentTokenTilDolly() {
        return dollyConsumer.hentTokenTilDolly();
    }
}
