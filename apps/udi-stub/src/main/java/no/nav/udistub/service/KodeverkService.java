package no.nav.udistub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.udistub.database.model.Kodeverk;
import no.nav.udistub.database.repository.KodeverkRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkService {

    private final KodeverkRepository kodeverkRepository;

    public List<Kodeverk> finnAlle() {
        ArrayList<Kodeverk> kodeverk = new ArrayList<>();
        kodeverkRepository.findAll().forEach(kodeverk::add);
        return kodeverk;
    }

    public List<Kodeverk> finnAlleMedType(String type) {
        return kodeverkRepository.findAllByType(type);
    }

    public Kodeverk finnMedKode(String kode) {
        return kodeverkRepository.findByKode(kode).orElseThrow(() ->
                new HttpClientErrorException(HttpStatus.NOT_FOUND,
                        String.format("Kunne ikke finne kode: %s i kodeverket", kode))
        );
    }

    public List<Kodeverk> finnAlleAktivTom(LocalDate tom) {
        return kodeverkRepository.findAllByAktivTomBefore(tom);
    }

    public List<Kodeverk> finnAlleAktivFom(LocalDate fom) {
        return kodeverkRepository.findAllByAktivFomAfter(fom);
    }

    public List<Kodeverk> finnAlleAktivMellom(LocalDate fom, LocalDate tom) {
        return kodeverkRepository.findAllByAktivFomAfterAndAktivTomBefore(fom, tom);
    }

    public List<Kodeverk> finnAlleAktive() {
        return kodeverkRepository.findAllByAktivTomIsNull();
    }

    public List<Kodeverk> finnAlleInaktive() {
        return kodeverkRepository.findAllByAktivTomIsNotNull();
    }
}
