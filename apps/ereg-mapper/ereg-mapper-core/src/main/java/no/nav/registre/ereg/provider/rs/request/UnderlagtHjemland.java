package no.nav.registre.ereg.provider.rs.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UnderlagtHjemland {

    private String underlagtLovgivningLandkoode;
    private String foretaksformHjemland;
    private String beskrivelseHjemland;
    private String beskrivelseNorge;
}
