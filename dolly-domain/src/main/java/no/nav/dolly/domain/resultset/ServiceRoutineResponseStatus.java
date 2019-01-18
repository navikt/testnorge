package no.nav.dolly.domain.resultset;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRoutineResponseStatus {
    private String personId;
    private String serviceRutinenavn;
    private Map<String, String> status; //Map<Environment, TPS respons statusmelding >
}
