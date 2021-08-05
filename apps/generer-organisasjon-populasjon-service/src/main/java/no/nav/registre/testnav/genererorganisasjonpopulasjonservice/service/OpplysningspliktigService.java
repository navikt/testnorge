package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.domain.Organisasjon;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.repository.OpplysningspliktigRepository;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.repository.model.OpplysningspliktigModel;
import no.nav.testnav.libs.avro.organisasjon.v1.Metadata;
import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.testnav.libs.kafkaproducers.organisasjon.v2.OpprettelsesdokumentV2Producer;

@Service
@RequiredArgsConstructor
public class OpplysningspliktigService {
    private final OpprettelsesdokumentV2Producer opprettelsesdokumentProducer;
    private final OpplysningspliktigRepository repository;

    public UUID create(List<Organisasjon> list, String miljo) {
        var uuid = UUID.randomUUID();
        list.forEach(organisasjon -> create(organisasjon, uuid, miljo));
        return uuid;
    }

    private void create(Organisasjon organisasjon, UUID uuid, String miljo) {
        var opprettelsesdokument = Opprettelsesdokument
                .newBuilder()
                .setOrganisasjon(organisasjon.toAvroOrganisasjon())
                .setMetadata(Metadata.newBuilder().setMiljo(miljo).build())
                .build();
        opprettelsesdokumentProducer.send(uuid.toString(), opprettelsesdokument);
        repository.save(OpplysningspliktigModel
                .builder()
                .miljo(miljo)
                .orgnummer(organisasjon.getOrgnummer())
                .build()
        );
    }

    public Set<String> getOpplysningspliktigOrgnummer(String miljo){
        return repository.findAllByMiljo(miljo)
                .stream()
                .map(OpplysningspliktigModel::getOrgnummer)
                .collect(Collectors.toSet());
    }

    public void deleteBy(String miljo) {
        repository.deleteAllByMiljo(miljo);
    }
}
