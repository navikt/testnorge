package no.nav.registre.ereg.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JenkinsCrumbRequest {
    private String _class;
    private String crumb;
    private String crumbRequestField;
}
