import React, { useEffect, useRef } from 'react'
import { useMount } from 'react-use'
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
	ident,
	gruppeId,
	brukertype,
	bestilling,
	bestillingsListe,
	gruppeIdenter,
	loading,
	slettPerson,
	leggTilPaaPerson,
	importerPartner,
	iLaastGruppe,
	tmpPersoner,
}) => {
	useMount(fetchDataFraFagsystemer)

	const mountedRef = useRef(true)
	const navigate = useNavigate()

	useEffect(() => {
		return () => {
			mountedRef.current = false
		}
	}, [])

	const pdlPartner = () => {
		if (ident.master === 'PDL') {
			return data.pdl?.hentPerson?.sivilstand?.filter(
				(siv) => !siv?.metadata?.historisk && ['GIFT', 'SEPARERT'].includes(siv?.type)
			)?.[0]?.relatertVedSivilstand
		} else {
			return null
		}
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
									bestillingsListe,
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

					{!iLaastGruppe && ident.master === 'PDL' && (
						<PartnerImportButton
							action={(partnerIdent) => {
								importerPartner(gruppeId, partnerIdent)
							}}
							partnerIdent={pdlPartner()}
							gruppeIdenter={gruppeIdenter}
							loading={loading.importerPartner}
						/>
					)}
					<BestillingSammendragModal bestilling={bestilling} />
					{!iLaastGruppe && ident.master !== 'PDL' && (
						<SlettButton action={slettPerson} loading={loading.slettPerson}>
							Er du sikker på at du vil slette denne personen?
						</SlettButton>
					)}
					{!iLaastGruppe && ident.master === 'PDL' && (
						<FrigjoerButton action={slettPerson} loading={loading.slettPerson} />
					)}
				</div>
				{ident.master !== 'PDL' && (
					<TpsfVisning
						data={TpsfVisning.filterValues(data.tpsf, bestillingsListe)}
						pdlData={data.pdlforvalter?.person}
						environments={bestilling?.environments}
						tmpPersoner={tmpPersoner?.pdlforvalter}
					/>
				)}
				{ident.master !== 'PDL' && (
					<PdlfVisningConnector data={data.pdlforvalter} loading={loading.pdlforvalter} />
				)}
				{ident.master === 'PDL' && (
					<PdlVisning pdlData={data.pdl} environments={bestilling?.environments} />
				)}
				<AaregVisning liste={data.aareg} loading={loading.aareg} />
				<SigrunstubVisning data={data.sigrunstub} loading={loading.sigrunstub} />
				<PensjonVisning data={data.pensjonforvalter} loading={loading.pensjonforvalter} />
				<InntektstubVisning liste={data.inntektstub} loading={loading.inntektstub} />
				<InntektsmeldingVisning
					liste={InntektsmeldingVisning.filterValues(bestillingsListe, ident.ident)}
					ident={ident.ident}
				/>
				<SykemeldingVisning data={SykemeldingVisning.filterValues(bestillingsListe, ident.ident)} />
				<BrregVisning data={data.brregstub} loading={loading.brregstub} />
				<KrrVisning data={data.krrstub} loading={loading.krrstub} />
				<InstVisning data={data.instdata} loading={loading.instdata} />
				<ArenaVisning
					data={data.arenaforvalteren}
					bestillinger={bestillingsListe}
					loading={loading.arenaforvalteren}
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
