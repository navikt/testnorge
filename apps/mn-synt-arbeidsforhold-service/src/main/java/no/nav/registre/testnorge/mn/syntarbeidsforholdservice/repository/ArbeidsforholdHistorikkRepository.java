package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.repository.model.ArbeidsforholdHistorikkModel;

public interface ArbeidsforholdHistorikkRepository extends CrudRepository<ArbeidsforholdHistorikkModel, Long> {

    Optional<ArbeidsforholdHistorikkModel> findByArbeidsforholdIdAndMiljo(String arbeidsforholdId, String miljo);

    List<ArbeidsforholdHistorikkModel> findAllByMiljo(String miljo);

    @Transactional
    void deleteAllByMiljo(String miljo);

    @Transactional
    void deleteByArbeidsforholdIdAndMiljo(String arbeidsforholdId, String miljo);
}