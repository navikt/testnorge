package no.nav.registre.inntektsmeldingstub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

import no.nav.inntektsmelding.xml.kodeliste._20190409.XMLNaturalytelseKodeliste;

@Entity
@Table(name = "naturalytelse_detaljer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaturalytelseDetaljer {

    @Id
    @GeneratedValue
    private Integer id;

    private XMLNaturalytelseKodeliste type;
    private LocalDate fom;
    private double beloepPrMnd;

}
