package no.nav.registre.sdforvalter.consumer.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.consumer.rs.domain.OrgTree;
import no.nav.registre.sdforvalter.consumer.rs.domain.OrgTreeList;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.EndringsdokumentProducer;
import no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v1.OpprettelsesdokumentProducer;

@Component
@RequiredArgsConstructor
public class OrganisasjonMottakServiceConsumer {

    private final OpprettelsesdokumentProducer opprettelsesdokumentProducer;
    private final EndringsdokumentProducer endringsdokumentProducer;
    private final GenererNavnConsumer genererNavnConsumer;

    private String genererNavn(String enhetstype) {
        NavnDTO navn = genererNavnConsumer.genereNavn();
        return navn.getAdjektiv() + " " + navn.getSubstantiv() + (enhetstype.equals("AS") ? " AS" : "");
    }

    private Adresse createAdresse(no.nav.registre.sdforvalter.domain.Adresse adresse) {
        if (adresse == null) {
            return null;
        }
        return Adresse.newBuilder()
                .setPostadresse1(adresse.getAdresse())
                .setKommunenummer(adresse.getKommunenr())
                .setPoststed(adresse.getPoststed())
                .setPostnummer(adresse.getPostnr())
                .setLandkode(adresse.getLandkode())
                .build();
    }

    public void create(Ereg ereg, String env) {
        save(new EregListe(ereg), env, false);
    }

    public void create(EregListe eregListe, String env) {
        save(eregListe, env, false);
    }

    public void update(Ereg ereg, String env) {
        update(new EregListe(ereg), env);
    }

    public void update(EregListe eregListe, String env) {
        save(eregListe, env, true);
    }

    public void save(EregListe eregListe, String env, boolean update) {
        save(new OrgTreeList(eregListe), env, update);
    }

    private void save(OrgTreeList orgTreeList, String env, boolean update) {
        orgTreeList.getList().forEach(value -> save(value, env, update));
    }

    private void save(OrgTree orgTree, String env, boolean update) {
        Metadata metadata = Metadata.newBuilder().setMiljo(env).build();

        UUID uuid = UUID.randomUUID();
        Organisasjon organisasjon = createOrganisasjon(orgTree);

        if (update) {
            endringsdokumentProducer.send(uuid.toString(), Endringsdokument.newBuilder()
                    .setOrganisasjon(organisasjon)
                    .setMetadata(metadata)
                    .build()
            );
        } else {
            opprettelsesdokumentProducer.send(uuid.toString(), Opprettelsesdokument.newBuilder()
                    .setOrganisasjon(organisasjon)
                    .setMetadata(metadata)
                    .build()
            );
        }


    }

    public Organisasjon createOrganisasjon(OrgTree orgTree) {
        Ereg ereg = orgTree.getOrganisasjon();

        String navn = ereg.getNavn() != null ? ereg.getNavn() : genererNavn(ereg.getEnhetstype());
        Adresse forretningsAdresse = createAdresse(ereg.getForretningsAdresse());
        Adresse postadresse = createAdresse(ereg.getPostadresse());

        Organisasjon.Builder builder = Organisasjon.newBuilder();
        return builder
                .setEnhetstype(ereg.getEnhetstype())
                .setOrgnummer(ereg.getOrgnr())
                .setEpost(ereg.getEpost() != null ? Epost.newBuilder().setEpost(ereg.getEpost()).build() : null)
                .setNavn(DetaljertNavn
                        .newBuilder()
                        .setNavn1(navn)
                        .setRedigertNavn(ereg.getRedigertNavn())
                        .build()
                )
                .setUnderenheter(orgTree.getUnderorganisasjon().stream().map(this::createOrganisasjon).collect(Collectors.toList()))
                .setInternettadresse(ereg.getInternetAdresse() != null ? Internettadresse.newBuilder().setInternettadresse(ereg.getInternetAdresse()).build() : null)
                .setForretningsadresse(forretningsAdresse)
                .setPostadresse(postadresse)
                .build();
    }
}
