package no.nav.registre.aaregstub.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusResponse {

    private List<String> identerLagretIStub;

    private List<String> identerLagretIAareg;

    private Map<String, String> identerSomIkkeKunneLagresIAareg;
}
