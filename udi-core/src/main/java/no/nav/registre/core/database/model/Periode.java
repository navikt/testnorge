package no.nav.registre.core.database.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.sql.Date;

@Embeddable
@Builder
@Getter
@Setter
public class Periode {

    private Date fra;
    private Date til;
}
