package no.nav.udistub.database.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.Varighet;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UtvistMedInnreiseForbud {

    @AttributeOverride(name = "value", column = @Column(name = "innreise_forbud"))
    private JaNeiUavklart innreiseForbud;

    private LocalDate innreiseForbudVedtaksDato;

    @AttributeOverride(name = "value", column = @Column(name = "innreise_forbud_varighet"))
    private Varighet varighet;
}
