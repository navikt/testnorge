package no.nav.organisasjonforvalter.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestillingRequest {

    private String id;
    private String parentId;
    private String enhetstype;
}
