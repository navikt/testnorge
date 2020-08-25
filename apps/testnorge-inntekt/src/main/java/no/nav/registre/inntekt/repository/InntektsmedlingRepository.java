package no.nav.registre.inntekt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.registre.inntekt.repository.model.InntektsmeldingModel;

public interface InntektsmedlingRepository extends JpaRepository<InntektsmeldingModel, Long> {

}