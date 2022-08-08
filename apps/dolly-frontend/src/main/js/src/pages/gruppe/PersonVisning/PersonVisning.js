import React, { useEffect, useRef } from 'react'
import Button from '~/components/ui/button/Button'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { PersonMiljoeinfo } from './PersonMiljoeinfo/PersonMiljoeinfo'
import {
	AaregVisning,
	ArenaVisning,
	BrregVisning,
	DokarkivVisning,
	InntektsmeldingVisning,
	InntektstubVisning,
	InstVisning,
	KrrVisning,
	PensjonVisning,
	SigrunstubVisning,
	SykemeldingVisning,
	TpsfVisning,
	UdiVisning,
} from '~/components/fagsystem'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/BestillingSammendragModal'
import './PersonVisning.less'
import { PdlPersonMiljoeInfo } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlPersonMiljoeinfo'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'
import PdlfVisningConnector from '~/components/fagsystem/pdlf/visning/PdlfVisningConnector'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { FrigjoerButton } from '~/components/ui/button/FrigjoerButton/FrigjoerButton'
import { useNavigate } from 'react-router-dom'
import { useBestillingerGruppe } from '~/utils/hooks/useBestilling'
import { getBestillingsListe } from '~/ducks/bestillingStatus'
import { PartnerImportButton } from '~/components/ui/button/PartnerImportButton/PartnerImportButton'

const getIdenttype = (ident) => {
	if (parseInt(ident.charAt(0)) > 3) {
		return 'DNR'
	} else if (parseInt(ident.charAt(2)) % 4 >= 2) {
		return 'NPID'
	} else {
		return 'FNR'
	}
}

export const PersonVisning = ({
	fetchDataFraFagsystemer,
	data,
	bestillingIdListe,
	ident,
	isAlive,
	brukertype,
	gruppeIdenter,
	loading,
	slettPerson,
	slettPersonOgPartner,
	leggTilPaaPerson,
	iLaastGruppe,
}) => {
	const { gruppeId } = ident

	const { bestillingerById } = useBestillingerGruppe(gruppeId)

	useEffect(() => {
		fetchDataFraFagsystemer(bestillingerById)
	}, [])

	const bestillingListe = getBestillingsListe(bestillingerById, bestillingIdListe)
	const bestilling = bestillingerById?.[bestillingIdListe?.[0]]

	const mountedRef = useRef(true)
	const navigate = useNavigate()

	useEffect(() => {
		return () => {
			mountedRef.current = false
		}
	}, [])

	const pdlPartner = () => {
		return data.pdl?.hentPerson?.sivilstand?.filter(
			(siv) =>
				!siv?.metadata?.historisk &&
				['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER'].includes(siv?.type)
		)?.[0]?.relatertVedSivilstand
	}

	return (
		<ErrorBoundary>
			<div className="person-visning">
				<div className="person-visning_actions">
					{!iLaastGruppe && (
						<Button
							onClick={() =>
								leggTilPaaPerson(
									data,
									bestillingListe,
									ident.master,
									getIdenttype(ident.ident),
									gruppeId,
									navigate
								)
							}
							kind="add-circle"
						>
							LEGG TIL/ENDRE
						</Button>
					)}

					{!iLaastGruppe && isAlive && (
						<PartnerImportButton
							gruppeId={gruppeId}
							partnerIdent={pdlPartner()}
							gruppeIdenter={gruppeIdenter}
							master={ident?.master}
						/>
					)}
					<BestillingSammendragModal bestilling={bestilling} />
					{!iLaastGruppe && ident.master !== 'PDL' && (
						<SlettButton action={slettPerson} loading={loading.slettPerson}>
							Er du sikker på at du vil slette denne personen?
						</SlettButton>
					)}
					{!iLaastGruppe && ident.master === 'PDL' && (
						<FrigjoerButton
							slettPerson={slettPerson}
							slettPersonOgPartner={slettPersonOgPartner}
							loading={loading.slettPerson || loading.slettPersonOgPartner}
							importertPartner={gruppeIdenter.includes(pdlPartner()) ? pdlPartner() : null}
						/>
					)}
				</div>
				{ident.master !== 'PDL' && (
					<PdlfVisningConnector
						data={data.pdlforvalter}
						tpsfData={TpsfVisning.filterValues(data.tpsf, bestillingListe)}
						loading={loading.pdlforvalter}
						environments={bestilling?.environments}
						master={ident.master}
					/>
				)}
				{ident.master === 'PDL' && (
					<PdlVisning pdlData={data.pdl} environments={bestilling?.environments} />
				)}
				<AaregVisning liste={data.aareg} loading={loading.aareg} />
				<SigrunstubVisning data={data.sigrunstub} loading={loading.sigrunstub} />
				<PensjonVisning data={data.pensjonforvalter} loading={loading.pensjonforvalter} />
				<InntektstubVisning liste={data.inntektstub} loading={loading.inntektstub} />
				<InntektsmeldingVisning
					liste={InntektsmeldingVisning.filterValues(bestillingListe, ident.ident)}
					ident={ident.ident}
				/>
				<SykemeldingVisning data={SykemeldingVisning.filterValues(bestillingListe, ident.ident)} />
				<BrregVisning data={data.brregstub} loading={loading.brregstub} />
				<KrrVisning data={data.krrstub} loading={loading.krrstub} />
				<InstVisning data={data.instdata} loading={loading.instdata} />
				<ArenaVisning
					data={data.arenaforvalteren}
					bestillinger={bestillingListe}
					loading={loading.arenaforvalteren}
					ident={ident}
				/>
				<UdiVisning
					data={UdiVisning.filterValues(data.udistub, bestilling?.bestilling.udistub)}
					loading={loading.udistub}
				/>
				<DokarkivVisning ident={ident.ident} />
				<PersonMiljoeinfo bankIdBruker={brukertype === 'BANKID'} ident={ident.ident} />
				<PdlPersonMiljoeInfo ident={ident.ident} />
				<TidligereBestillinger ids={ident.bestillingId} />
				<BeskrivelseConnector ident={ident} />
			</div>
		</ErrorBoundary>
	)
}
