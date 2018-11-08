package no.nav.registre.orkestratoren.consumer.rs.requests;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendToTpsRequest {
    private String environment;
    private List<Long> ids;
}
