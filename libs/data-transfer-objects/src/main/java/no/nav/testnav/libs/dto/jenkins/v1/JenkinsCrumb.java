package no.nav.testnav.libs.dto.jenkins.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class JenkinsCrumb {
    private String _class;
    private String crumb;
    private String crumbRequestField;
}
