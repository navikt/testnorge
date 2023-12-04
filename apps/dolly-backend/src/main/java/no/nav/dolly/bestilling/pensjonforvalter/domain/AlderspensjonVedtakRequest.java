package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlderspensjonVedtakRequest extends AlderspensjonRequest {

    private LocalDate kravFremsattDato;

    private String saksbehandler;
    private String attesterer;

    private String navEnhetId;
}
