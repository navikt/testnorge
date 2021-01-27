package no.nav.registre.testnorge.arbeidsforhold.repository.elastic.model;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Document(indexName = "oppsummeringsdokument")
public class OppsummeringsdokumentModel {

    @Id
    private String id;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
