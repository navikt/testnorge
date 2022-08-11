package no.nav.testnav.apps.tpservice.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity(name = "T_FORHOLD_YTELSE_HISTORIKK")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TForholdYtelseHistorikk {

    @EmbeddedId
    HistorikkComposityKey historikkComposityKey;

}
