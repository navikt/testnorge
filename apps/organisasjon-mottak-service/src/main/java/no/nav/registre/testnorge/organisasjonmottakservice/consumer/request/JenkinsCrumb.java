package no.nav.registre.testnorge.organisasjonmottakservice.consumer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class JenkinsCrumb {
    private String _class;
    private String crumb;
    private String crumbRequestField;
}
