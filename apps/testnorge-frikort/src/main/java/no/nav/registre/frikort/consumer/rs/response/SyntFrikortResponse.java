package no.nav.registre.frikort.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import no.nav.registre.frikort.domain.xml.Egenandelskode;
import no.nav.registre.frikort.domain.xml.Samhandlertypekode;
import no.nav.registre.frikort.service.util.JsonDateDeserializer;
import no.nav.registre.frikort.service.util.JsonDateSerializer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyntFrikortResponse {

    private String betalt;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonAlias("dato_mottatt")
    private LocalDateTime datoMottatt;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonAlias("datotjeneste")
    private LocalDateTime datoTjeneste;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonAlias("datotjenestestart")
    private LocalDateTime datoTjenestestart;

    private Double egenandelsats;
    private Double egenandelsbelop;
    private Egenandelskode egenandelskode;
    private String enkeltregningsstatuskode;
    private String innsendingstypekode;
    private String kildesystemkode;
    private String merknader;
    private Samhandlertypekode samhandlertypekode;
}
