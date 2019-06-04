package no.nav.registre.core.database.model;

import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
public class Periode {

    private Date fra;
    private Date til;

    private Date effektueringsdato;
}
