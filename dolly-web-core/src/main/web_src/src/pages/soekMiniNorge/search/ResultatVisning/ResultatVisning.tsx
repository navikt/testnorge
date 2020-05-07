import React from 'react'
import { useMount } from 'react-use'
import { KrrVisning } from '~/components/fagsystem/krrstub/visning/KrrVisning'
import { PdlfVisning } from '~/components/fagsystem/pdlf/visning/Visning'
import { ArenaVisning } from '~/components/fagsystem/arena/visning/ArenaVisning'
import { AaregVisning } from '~/components/fagsystem/aareg/visning/Visning'
import { UdiVisning } from '~/components/fagsystem/udistub/visning/UdiVisning'
import { SigrunstubVisning } from '~/components/fagsystem/sigrunstub/visning/Visning'
import { InntektstubVisning } from '~/components/fagsystem/inntektstub/visning/Visning'
import { InntektsmeldingVisning } from '~/components/fagsystem/inntektsmelding/visning/Visning'
import { InstVisning } from '~/components/fagsystem/inst/visning/InstVisning'
import { PensjonVisning } from '~/components/fagsystem/pensjon/visning/PensjonVisning'
import { BrregVisning } from '~/components/fagsystem/brregstub/visning/BrregVisning'
import Panel from '~/components/ui/panel/Panel'

import { MiniNorgeVisning } from '~/pages/soekMiniNorge/search/ResultatVisning/partials/MiniNorgeVisning'

type Loading = {
	pdlforvalter: boolean
	pensjonforvalter: boolean
	inntektstub: boolean
	krrstub: boolean
	instdata: boolean
	sigrunstub: boolean
	arenaforvalteren: boolean
	udistib: boolean
	bregstub: boolean
	aareg: boolean
}

interface ResultatVisningProps {
	fetchDataFraFagsystemerForSoek: any
	data: any
	dataFraMiniNorge: any
	ident: string
	loading: Loading
}

export const ResultatVisning = ({
	fetchDataFraFagsystemerForSoek,
	data,
	dataFraMiniNorge,
	ident,
	loading
}: ResultatVisningProps) => {
	useMount(fetchDataFraFagsystemerForSoek)

	return (
		<div className="resultat-visning">
			{dataFraMiniNorge && <MiniNorgeVisning data={dataFraMiniNorge}/>}
			{data.pdlforvalter && (
				//@ts-ignore
				<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />
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
			{data.sigrunstub && data.sigrunstub.length > 0 && (
				//@ts-ignore
				<Panel heading="Skatteoppgjør (Sigrun)">
					<SigrunstubVisning data={data.sigrunstub} loading={loading.sigrunstub} />
				</Panel>
			)}
		</div>
	)
}
//TODO:
// hente (se ducks/fagsystem/index) og vise data fra aareg, udistub, brreegstub
// vise data for arena, inntektsmelding