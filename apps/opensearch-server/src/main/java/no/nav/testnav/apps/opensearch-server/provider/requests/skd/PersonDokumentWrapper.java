package no.nav.testnav.apps.hodejegeren.provider.requests.skd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Boadresse;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Doedshistorikk;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Foreldreansvar;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Giro;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Migrasjon;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Navn;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Oppholdstillatelse;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.PersonIdent;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.PersonInfo;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Post;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.PrioritertAdresse;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Relasjon;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Sivilstand;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Statsborger;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.TelefonJobb;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.TelefonMobil;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.TelefonPrivat;
import no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold.Tillegg;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDokumentWrapper {

    private PersonIdent personIdent;
    private PersonInfo personInfo;
    private Navn navn;
    private Sivilstand sivilstand;
    private Statsborger statsborger;
    private Doedshistorikk doedshistorikk;
    private TelefonPrivat telefonPrivat;
    private TelefonJobb telefonJobb;
    private TelefonMobil telefonMobil;
    private Boadresse boadresse;
    private PrioritertAdresse prioritertAdresse;
    private List<Foreldreansvar> foreldreansvar;
    private Oppholdstillatelse oppholdstillatelse;
    private Giro giro;
    private Tillegg tillegg;
    private Post post;
    private Migrasjon migrasjon;
    private List<Relasjon> relasjoner;
}
