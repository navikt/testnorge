package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.DollyTeamFragment;
import no.nav.dolly.domain.projection.OrganisasjonFragment;
import no.nav.dolly.domain.projection.RsBestillingFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Repository
public interface BestillingRepository extends ReactiveSortingRepository<Bestilling, Long> {

    Mono<Void> deleteAll();

    Mono<Bestilling> findById(Long id);

    Flux<Bestilling> findByOrderByIdDesc();

    Mono<Void> deleteById(Long id);

    Flux<Bestilling> findByGruppeId(Long gruppeId);

    Mono<Integer> countByGruppeId(Long gruppeId);

    @Query("""
            select b.id as id, g.navn as navn
            from Bestilling b
            join Gruppe g on b.gruppe_id = g.id
            where length(:id) > 0
            and cast(b.id as VARCHAR) ilike :id
            fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByIdContaining(@Param("id") String id);

    @Query("""
            select b.id as id, g.navn as navn
            from Bestilling b
            join Gruppe g on b.gruppe_id = g.id
            where length(:gruppenavn) > 0
            and g.navn ilike :gruppenavn
            fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByGruppenavnContaining(@Param("gruppenavn") String gruppenavn);

    @Query("""
            select b.id as id, g.navn as navn
            from Bestilling b
            join gruppe g on b.gruppe_id = g.id
            where cast(b.id as varchar) like :id
            and g.navn like :gruppenavn
            fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByIdContainingAndGruppeNavnContaining(
            @Param("id") String id,
            @Param("gruppenavn") String gruppenavn
    );

    Mono<Bestilling> save(Bestilling bestilling);

    @Query("""
            select position-1
            from (
            select b.id, row_number() over (order by b.id desc) as position
            from bestilling b
            where b.gruppe_id = :gruppeId
            ) result
            where id = :bestillingId
            """)
    Mono<Integer> getPaginertBestillingIndex(@Param("bestillingId") Long bestillingId, @Param("gruppeId") Long gruppe);

    @Query("""
            select * from Bestilling b
            join Bestilling_Progress bp on b.id = bp.bestilling_id
            where bp.ident = :ident
            order by b.id
            """)
    Flux<Bestilling> findBestillingerByIdent(@Param("ident") String ident);

    @Query("""
            select b.* from Bestilling b
            join Bestilling_Progress bp on b.id = bp.bestilling_id
            and bp.ident in (:identer) order by b.id
            """)
    Flux<Bestilling> findBestillingerByIdentIn(@Param("identer") Collection<String> identer);

    Flux<Bestilling> findByGruppeIdOrderByIdDesc(Long gruppeId, Pageable pageable);

    @Modifying
    Mono<Void> deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query("""
            delete from Bestilling b
            where b.id = :bestillingId
            and not exists (select *
                            from Bestilling_Progress bp
                            where bp.bestilling_id = :bestillingId)
            and not exists (select *
                            from transaksjon_mapping tm
                            where tm.bestilling_id = :bestillingId)
            """)
    Mono<Void> deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);

    @Modifying
    @Query("""
            update Bestilling b
            set ident = :newIdent where ident = :oldIdent
            """)
    Mono<Bestilling> swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query("""
            select * from bestilling b
            where id = :id for update
            """)
    Mono<Bestilling> findByIdAndLock(@Param("id") Long id);

    @Modifying
    @Query("""
            update Bestilling b
            set ferdig = true,
            stoppet = true
            where b.ferdig = false
            """)
    Mono<Integer> stopAllUnfinished();

    Flux<Bestilling> findByIdIn(List<Long> id);

    @Query("""
            select count(*) personer,
                   b.sist_oppdatert::date dato,
                   case
                      when b.opprettet_fra_id is not null then 'GJENOPPRETTING'
                      when b.gjenopprettet_fra_ident is not null then 'GJENOPPRETTING'
                      when b.opprett_fra_gruppe is not null then 'GJENOPPRETTING'
                      else 'NYBESTILLING'
                   end as gjenopprettStatus,
                   case
                      when lower(bp.pdl_person_status) not like 'synkronisering%' then 'FEIL'
                      when lower(bp.pdl_forvalter_status) like '%feil%' then 'FEIL'
                      when lower(bp.pdl_ordre_status) like '%feil%' then 'FEIL'
                      else 'OK'
                   end as pdlStatus,
                   case
                      when lower(bp.pensjonforvalter_status) like '%feil%' then 'FEIL'
                      when lower(bp.aareg_status) like '%feil%' then 'FEIL'
                      when lower(bp.arenaforvalter_status) like '%feil%' then 'FEIL'
                      when lower(bp.instdata_status) like '%feil%' then 'FEIL'
                      when lower(bp.inntektsstub_status) like '%feil%' then 'FEIL'
                      when lower(bp.inntektsmelding_status) like '%feil%' then 'FEIL'
                      when lower(bp.sigrunstub_status) like '%feil%' then 'FEIL'
                      when lower(bp.dokarkiv_status) like '%feil%' then 'FEIL'
                      when lower(bp.feil) like '%feil%' then 'FEIL'
                      else 'OK'
                   end as annenStatus
            from bestilling b
            join bestilling_progress bp on b.id = bp.bestilling_id
            group by dato, gjenopprettStatus, pdlStatus, annenStatus
            order by dato desc
            """)
    Flux<BestillingerFragment> findBestillingerOrderBySistOppdatert();


    @Query("""
            select count(*) antall, to_char(b.sist_oppdatert, 'YYYY-MM') interval, br.epost
            from bestilling b
            join bruker br on br.id = b.bruker_id
            and br.brukertype = 'AZURE'
            and br.epost is not null
            group by interval, br.epost
            order by interval desc;
            """)
    Flux<TeamFragment> findBestillingerForTeamsOrderBySistOppdatert();

    @Query("""
            select count(*) antall, to_char(b.sist_oppdatert, 'YYYY-MM') interval, br.bruker_id brukerId
            from bestilling b
            join bruker br on br.id = b.bruker_id
            and br.brukertype = 'BANKID'
            group by interval, brukerId
            order by interval desc;
            """)
    Flux<OrganisasjonFragment> findBestillingerForOrganisasjonerOrderBySistOppdatert();

    @Query("""
            select count(*) antall, to_char(b.sist_oppdatert, 'YYYY-MM') interval,
                   t.navn || '|' || coalesce(t.beskrivelse, '') || '|' || t.bruker_id informasjon
            from bestilling b
            join bruker br on br.id = b.bruker_id
            join team t on t.bruker_id = br.id
            and br.brukertype = 'TEAM'
            group by interval, informasjon
            order by interval desc;
            """)
    Flux<DollyTeamFragment> findBestillingerForDollyTeamsOrderBySistOppdatert();
}