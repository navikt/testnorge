package no.nav.testnav.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.ParameterDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.testnav.levendearbeidsforholdansettelse.repository.ParameterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service klassen til jobberRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParameterService {

    private final ParameterRepository parameterRepository;

    /**
     * Henter alle parameterne i jobb_porameter db.
     *
     * @return Returnerer en liste av JobbParameterObjektene til alle parameterne
     */
    public List<ParameterDTO> hentAlleParametere() {

        return parameterRepository.findAll().stream()
                .map(parameter -> ParameterDTO.builder()
                        .navn(parameter.getNavn())
                        .tekst(parameter.getTekst())
                        .verdi(parameter.getVerdi())
                        .verdier(Arrays.asList(parameter.getVerdier().split(",")))
                        .build())
                .toList();
    }

    /**
     * Funksjon for å lage ett map av navnet og verdien til parameterne for å gjøre det lettere å bruke.
     *
     * @return ett map av JobbParameter navnet og verdier
     */
    public Map<String, String> hentParametere() {

        return parameterRepository.findAll().stream()
                .collect(Collectors.toMap(JobbParameter::getNavn, JobbParameter::getVerdi));
    }

    /**
     * Funksjon for å oppdatere en verdi i db.
     *
     * @param parameternavn Objektet som skal oppdateres i
     * @param verdi         ny verdi for parameter
     */

    @Transactional
    public void updateVerdi(String parameternavn, String verdi) {

        parameterRepository.findById(parameternavn)
                .ifPresentOrElse(parameter -> parameter.setVerdi(verdi),
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Parameter med navn %s ble ikke funnet".formatted(parameternavn));
                        });
    }
}
