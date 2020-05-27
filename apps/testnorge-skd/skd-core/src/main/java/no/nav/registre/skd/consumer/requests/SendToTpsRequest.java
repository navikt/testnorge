package no.nav.registre.skd.consumer.requests;

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
