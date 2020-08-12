package no.nav.registre.testnorge.rapportering.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "report")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReportModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "application_name", nullable = false, updatable = false)
    private String applicationName;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Column(name = "trace_id", updatable = false)
    private String traceId;

    @Column(name = "run_start", nullable = false, updatable = false)
    private Timestamp start;

    @Column(name = "run_end", nullable = false, updatable = false)
    private Timestamp end;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Date createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "report_id")
    private List<EntryModel> entrys;
}
