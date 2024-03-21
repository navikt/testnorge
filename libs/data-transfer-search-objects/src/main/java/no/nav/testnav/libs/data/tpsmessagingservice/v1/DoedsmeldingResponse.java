package no.nav.testnav.libs.data.tpsmessagingservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoedsmeldingResponse {

    private String ident;
    private Map<String, String> miljoStatus;
}
