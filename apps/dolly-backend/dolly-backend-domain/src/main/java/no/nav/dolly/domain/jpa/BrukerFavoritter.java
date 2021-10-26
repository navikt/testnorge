package no.nav.dolly.domain.jpa;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BRUKER_FAVORITTER")
public class BrukerFavoritter {

    @EmbeddedId
    BrukerFavoritterId id;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class BrukerFavoritterId implements Serializable {

        @Column(name = "BRUKER_ID")
        private Long brukerId;

        @Column(name= "GRUPPE_ID")
        private Long gruppeId;
    }
}
