package no.nav.testnav.apps.oppsummeringsdokumentservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {

    private String id;
    private String miljo;
    private LocalDate fom;
    private LocalDate tom;
    private String ident;
    private String orgnummer;
    private String typeArbeidsforhold;
    private Integer page;
    private Integer pageSize;
}
