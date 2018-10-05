package no.nav.registre.orkestratoren.consumer.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendToTpsRequest {
    private String environment;
    private List<Long> ids;
}
