package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public class RsBestilling {

    private Long id;
    private Integer antallIdenter;
    private boolean ferdig;
    private LocalDateTime sistOppdatert;
    private long gruppeId;
    private boolean stoppet;
    private String feil;
    private List<String> environments;
    private Set<RsStatusMiljoeIdent> tpsfStatus;
    private Set<RsStatusIdent> krrStubStatus;
    private Set<RsStatusIdent> sigrunStubStatus;
    private Set<RsStatusMiljoeIdentForhold> aaregStatus;
    private Set<RsMeldingStatusIdent> arenaforvalterStatus;
    private RsPdlForvalterStatus pdlforvalterStatus;

    private List<RsBestillingProgress> bestillingProgress;
    private Long opprettetFraId;
    private String tpsfKriterier;
    private String bestKriterier;
    private String openamSent;
    private String opprettFraIdenter;

    private String malBestillingNavn;

    public Set<RsStatusMiljoeIdent> getTpsfStatus() {
        if (isNull(tpsfStatus)) {
            tpsfStatus = new HashSet<>();
        }
        return tpsfStatus;
    }

    public Set<RsStatusIdent> getKrrStubStatus() {
        if (isNull(krrStubStatus)) {
            krrStubStatus = new HashSet<>();
        }
        return krrStubStatus;
    }

    public Set<RsStatusIdent> getSigrunStubStatus() {
        if (isNull(sigrunStubStatus)) {
            sigrunStubStatus = new HashSet<>();
        }
        return sigrunStubStatus;
    }

    public Set<RsStatusMiljoeIdentForhold> getAaregStatus() {
        if (isNull(aaregStatus)) {
            aaregStatus = new HashSet<>();
        }
        return aaregStatus;
    }

    public Set<RsMeldingStatusIdent> getArenaforvalterStatus() {
        if (isNull(arenaforvalterStatus)) {
            arenaforvalterStatus = new HashSet<>();
        }
        return arenaforvalterStatus;
    }
}
