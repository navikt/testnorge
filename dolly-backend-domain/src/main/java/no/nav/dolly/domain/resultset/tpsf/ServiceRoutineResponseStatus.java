package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRoutineResponseStatus {

    private String personId;
    private String serviceRutinenavn;
    private Map<String, String> status; //Map<Environment, TPS respons statusmelding >
}
