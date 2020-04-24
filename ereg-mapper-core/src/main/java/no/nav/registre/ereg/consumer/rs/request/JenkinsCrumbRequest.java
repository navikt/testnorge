package no.nav.registre.ereg.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class JenkinsCrumbRequest {
    private String _class;
    private String crumb;
    private String crumbRequestField;
}
