package no.nav.testnav.libs.dto.status.v1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestnavStatusResponse {

    private String alive;
    private String ready;
    private String team;
}
