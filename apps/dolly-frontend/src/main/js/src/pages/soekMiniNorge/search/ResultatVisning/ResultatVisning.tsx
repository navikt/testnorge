import React from 'react'
import { useMount } from 'react-use'
import {
	AaregVisning,
	InntektstubVisning,
	InstVisning,
	KrrVisning,
	PdlfVisning,
	PensjonVisning,
	SigrunstubVisning,
} from '~/components/fagsystem'
import Panel from '~/components/ui/panel/Panel'

import {
	ArenaVisning,
	MiniNorgeVisning,
} from '~/pages/soekMiniNorge/search/ResultatVisning/partials'
import { Innhold } from '~/pages/soekMiniNorge/hodejegeren/types'

type Loading = {
	pdlforvalter: boolean
	pensjonforvalter: boolean
	inntektstub: boolean
	krrstub: boolean
	instdata: boolean
	sigrunstub: boolean
	arenaforvalteren: boolean
	udistub: boolean
	bregstub: boolean
	aareg: boolean
}

interface ResultatVisningProps {
	fetchDataFraFagsystemerForSoek: () => void
	data: any
	dataFraMiniNorge: Innhold
	ident: string
	loading: Loading
}

export const ResultatVisning = ({
																	fetchDataFraFagsystemerForSoek,
																	data,
																	dataFraMiniNorge,
																	loading,
																}: ResultatVisningProps) => {
	useMount(fetchDataFraFagsystemerForSoek)
	return (
		<div className='resultat-visning'>
			{dataFraMiniNorge && <MiniNorgeVisning data={dataFraMiniNorge} />}
			{data.pdlforvalter && (
				//@ts-ignore
				<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />
			)}
			{data.aareg && data.aareg.length > 0 && (
				<AaregVisning liste={data.aareg} loading={loading.aareg} />
			)}
			{data.pensjonforvalter && data.pensjonforvalter.length > 0 && (
				//@ts-ignore
				<PensjonVisning data={data.pensjonforvalter} loading={loading.pensjonforvalter} />
			)}
			{data.inntektstub && data.inntektstub.length > 0 && (
				//@ts-ignore
				<InntektstubVisning data={data.inntektstub} loading={loading.inntektstub} />
			)}
			{data.krrstub && data.krrstub.length > 0 && (
				//@ts-ignore
				<KrrVisning data={data.krrstub} loading={loading.krrstub} />
			)}
			{data.instdata && data.instdata.length > 0 && (
				//@ts-ignore
				<InstVisning data={data.instdata} loading={loading.instdata} />
			)}
			{data.arenaforvalteren && data.arenaforvalteren.arbeidsokerList.length > 0 && (
				//@ts-ignore
				<ArenaVisning data={data.arenaforvalteren} loading={loading.arenaforvalteren} />
			)}
			{data.sigrunstub && data.sigrunstub.length > 0 && (
				//@ts-ignore
				<Panel heading='Skatteoppgjør (Sigrun)' iconType={'sigrun'}>
					{
						//@ts-ignore
						<SigrunstubVisning
							data={data.sigrunstub}
							loading={loading.sigrunstub}
							visTittel={false}
						/>
					}
				</Panel>
			)}
		</div>
	)
}
//TODO:
// vise data for inntektsmelding?
