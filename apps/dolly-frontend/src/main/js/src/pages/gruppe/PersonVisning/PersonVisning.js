import React from 'react'
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
	PdlfVisning,
	PensjonVisning,
	SigrunstubVisning,
	SykemeldingVisning,
	TpsfVisning,
	UdiVisning,
} from '~/components/fagsystem'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/BestillingSammendragModal'
import { LeggTilRelasjonModal } from '~/components/leggTilRelasjon/LeggTilRelasjonModal'

import './PersonVisning.less'
import { PdlPersonMiljoeInfo } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlPersonMiljoeinfo'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'

export const PersonVisning = ({
	fetchDataFraFagsystemer,
	data,
	ident,
	bestilling,
	bestillingsListe,
	loading,
	slettPerson,
	leggTilPaaPerson,
	iLaastGruppe,
}) => {
	useMount(fetchDataFraFagsystemer)

	const personInfo = data.tpsf
		? data.tpsf
		: {
				kjonn: data.pdlforvalter?.person?.kjoenn?.[0]?.kjoenn,
				ident: data.pdlforvalter?.person?.ident,
				fornavn: data.pdlforvalter?.person?.navn?.[0]?.fornavn,
				etternavn: data.pdlforvalter?.person?.navn?.[0]?.etternavn,
		  }

	return (
		<div className="person-visning">
			<div className="person-visning_actions">
				{!iLaastGruppe && ident.master !== 'PDLF' && (
					<Button
						onClick={() => leggTilPaaPerson(data, bestillingsListe, ident.master)}
						kind="add-circle"
					>
						LEGG TIL/ENDRE
					</Button>
				)}
				{!iLaastGruppe && ident.master === 'TPSF' && (
					<LeggTilRelasjonModal environments={bestilling?.environments} personInfo={personInfo} />
				)}
				<BestillingSammendragModal bestilling={bestilling} />
				{!iLaastGruppe && (
					<SlettButton action={slettPerson} loading={loading.slettPerson}>
						Er du sikker på at du vil slette denne personen?
					</SlettButton>
				)}
			</div>
			{ident.master !== 'PDL' && (
				<TpsfVisning
					data={TpsfVisning.filterValues(data.tpsf, bestillingsListe)}
					pdlData={data.pdlforvalter}
					environments={bestilling?.environments}
				/>
			)}
			{ident.master !== 'PDL' && (
				<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />
			)}
			{ident.master === 'PDL' && <PdlVisning pdlData={data.pdl} loading={loading.pdl} />}
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
			{data.tpsf && <PersonMiljoeinfo ident={ident.ident} miljoe={bestilling?.environments} />}
			<PdlPersonMiljoeInfo data={data.pdl} loading={loading.pdl} />
			<TidligereBestillinger ids={ident.bestillingId} />
			<BeskrivelseConnector ident={ident} />
		</div>
	)
}
