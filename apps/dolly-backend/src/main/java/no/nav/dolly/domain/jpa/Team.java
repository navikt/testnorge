package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
public class Team implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BRUKER_ID")
    private Long brukerId;

    @Column(nullable = false, unique = true)
    private String navn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opprettet_av")
    private Bruker opprettetAv;

    @Column
    private String beskrivelse;

    @ManyToMany
    @JoinTable(name = "TEAM_BRUKER",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "bruker_id"))
    @Builder.Default
    private Set<Bruker> brukere = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime opprettet;
}