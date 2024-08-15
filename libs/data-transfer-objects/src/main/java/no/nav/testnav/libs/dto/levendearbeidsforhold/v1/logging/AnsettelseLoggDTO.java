package no.nav.testnav.libs.dto.levendearbeidsforhold.v1.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnsettelseLoggDTO {

    private Integer id;
    private String organisajonsnummer;
    private String folkeregisterident;
    private LocalDateTime timestamp;
    private LocalDate ansattfra;
    private String arbeidsforholdstype;
    private Integer stillingsprosent;
}