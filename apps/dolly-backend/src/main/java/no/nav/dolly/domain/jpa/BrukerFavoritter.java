package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
