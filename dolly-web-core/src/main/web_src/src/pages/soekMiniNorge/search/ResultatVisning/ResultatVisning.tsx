import React from 'react'
import { useMount } from 'react-use'
import { TpsfVisning } from '~/components/fagsystem/tpsf/visning/Visning'
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

import './ResultatVisning.less'

interface ResultatVisningProps {
	fetchDataFraFagsystemer: any
	data: any
	ident: string
	loading: any
}

export const ResultatVisning = ({
	fetchDataFraFagsystemer,
	data,
	ident,
	loading
}: ResultatVisningProps) => {
	useMount(fetchDataFraFagsystemer)
	return (
		<div className="resultat-visning">
			{data.pdlforvalter && (
				//@ts-ignore
				<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />
			)}
			{data.aareg && data.aareg.length > 0 && (
				//@ts-ignore
				<AaregVisning data={data.aareg} loading={loading.aareg} />
			)}
			{data.sigrunstub && data.sigrunstub.length > 0 && (
				//@ts-ignore
				<SigrunstubVisning data={data.sigrunstub} loading={loading.sigrunstub} />
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
		</div>
	)
}
