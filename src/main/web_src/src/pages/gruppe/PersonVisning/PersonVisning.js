import React from 'react'
import { useMount } from 'react-use'
import Button from '~/components/ui/button/Button'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { PersonMiljoeinfo } from './PersonMiljoeinfo/PersonMiljoeinfo'
import {
	KrrVisning,
	PdlfVisning,
	SigrunstubVisning,
	InntektstubVisning,
	InstVisning,
	PensjonVisning,
	TpsfVisning,
	ArenaVisning,
	AaregVisning,
	UdiVisning,
	InntektsmeldingVisning,
	BrregVisning,
	DokarkivVisning,
	SykemeldingVisning
} from '~/components/fagsystem'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/SammendragModal'
import { LeggTilRelasjonModal } from '~/components/leggTilRelasjon/LeggTilRelasjonModal'

import './PersonVisning.less'

export const PersonVisning = ({
	fetchDataFraFagsystemer,
	data,
	ident,
	bestilling,
	bestillingsListe,
	loading,
	slettPerson,
	leggTilPaaPerson,
	iLaastGruppe
}) => {
	useMount(fetchDataFraFagsystemer)

	return (
		<div className="person-visning">
			<div className="person-visning_actions">
				{!iLaastGruppe && (
					<Button onClick={() => leggTilPaaPerson(data)} kind="add-circle">
						LEGG TIL/ENDRE
					</Button>
				)}
				{!iLaastGruppe && (
					<LeggTilRelasjonModal environments={bestilling.environments} personInfo={data.tpsf} />
				)}
				<BestillingSammendragModal bestilling={bestilling} />
				{!iLaastGruppe && (
					<SlettButton action={slettPerson} loading={loading.slettPerson}>
						Er du sikker på at du vil slette denne personen?
					</SlettButton>
				)}
			</div>
			<TpsfVisning data={TpsfVisning.filterValues(data.tpsf, bestillingsListe)} />
			<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />
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
				data={UdiVisning.filterValues(data.udistub, bestilling.bestilling.udistub)}
				loading={loading.udistub}
			/>
			<DokarkivVisning ident={ident.ident} />
			<PersonMiljoeinfo ident={ident.ident} miljoe={bestilling.environments} />
			<TidligereBestillinger ids={ident.bestillingId} />
			<BeskrivelseConnector ident={ident} iLaastGruppe={iLaastGruppe} />
		</div>
	)
}
