package no.nav.dolly.domain.jpa;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    @Id
    @Column(name = "NAV_IDENT", length = 10)
    private String brukerId;

    @ManyToMany
    @JoinTable(name = "T_BRUKER_FAVORITTER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id"))
    private Set<Testgruppe> favoritter;

    public Set<Testgruppe> getFavoritter() {
        if (favoritter == null) {
            favoritter = new HashSet<>();
        }
        return favoritter;
    }
}
