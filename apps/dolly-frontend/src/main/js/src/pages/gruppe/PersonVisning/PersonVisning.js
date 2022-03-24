import React, { useEffect, useState } from 'react'
import { useBoolean, useMount } from 'react-use'
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

import './PersonVisning.less'
import { PdlPersonMiljoeInfo } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlPersonMiljoeinfo'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'
import { DollyApi } from '~/service/Api'

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
	brukertype,
	bestilling,
	bestillingsListe,
	loading,
	slettPerson,
	leggTilPaaPerson,
	iLaastGruppe,
	setVisning,
}) => {
	useMount(fetchDataFraFagsystemer)

	const [pdlData, setPdlData] = useState(null)
	const [pdlLoading, setPdlLoading] = useBoolean(true)

	useEffect(() => {
		DollyApi.getPersonFraPdl(ident.ident)
			.then((response) => {
				setPdlData(response.data?.data)
				setPdlLoading(false)
			})
			.catch((e) => {
				setPdlLoading(false)
			})
	}, [])

	return (
		<div className="person-visning">
			<div className="person-visning_actions">
				{!iLaastGruppe && (
					<Button
						onClick={() =>
							leggTilPaaPerson(data, bestillingsListe, ident.master, getIdenttype(ident.ident))
						}
						kind="add-circle"
						disabled={ident.master === 'TPSF'}
						title={
							ident.master === 'TPSF'
								? 'Det er dessverre ikke lenger mulig å gjøre endringer på denne testpersonen. Master for bestillinger er endret til PDL, men denne personen er opprettet med TPS som master.'
								: null
						}
					>
						LEGG TIL/ENDRE
					</Button>
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
					pdlData={data.pdlforvalter?.person}
					environments={bestilling?.environments}
				/>
			)}
			{ident.master !== 'PDL' && (
				<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />
			)}
			{ident.master === 'PDL' && <PdlVisning pdlData={pdlData} loading={pdlLoading} />}
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
			<PdlPersonMiljoeInfo data={pdlData} loading={pdlLoading} />
			<TidligereBestillinger ids={ident.bestillingId} setVisning={setVisning} ident={ident.ident} />
			<BeskrivelseConnector ident={ident} />
		</div>
	)
}
