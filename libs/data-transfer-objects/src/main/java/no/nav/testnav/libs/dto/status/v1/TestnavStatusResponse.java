package no.nav.testnav.libs.dto.status.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestnavStatusResponse {

    private String alive;
    private String ready;
    private String team;
}
