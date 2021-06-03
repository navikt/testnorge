package no.nav.registre.testnav.inntektsmeldingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.registre.testnav.inntektsmeldingservice.repository.model.InntektsmeldingModel;

public interface InntektsmedlingRepository extends JpaRepository<InntektsmeldingModel, Long> {

}