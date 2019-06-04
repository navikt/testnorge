package no.nav.registre.core.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.Varighet;

import javax.persistence.Embeddable;
import java.sql.Date;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class UtvistMedInnreiseForbud {

    private Date vedtaksDato;

    private JaNeiUavklart innreiseForbud;

    private Varighet varighet;

    public void setVedtaksDato(Date vedtaksDato) {
        this.vedtaksDato = new Date(vedtaksDato.toInstant().getEpochSecond());
    }


}
