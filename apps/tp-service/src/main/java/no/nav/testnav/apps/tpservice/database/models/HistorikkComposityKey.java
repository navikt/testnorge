package no.nav.testnav.apps.tpservice.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
@AllArgsConstructor
public class HistorikkComposityKey implements Serializable {

    private Integer forholdIdFk;
    private Integer ytelseIdFk;

}
