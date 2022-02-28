import React from 'react'
import { useAsync } from 'react-use'
import Loading from '~/components/ui/loading/Loading'
import { TpsDataVisning } from './TpsDataVisning'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TpsMessagingApi } from '~/service/Api'

type PersonMiljoeinfoProps = {
	bankIdBruker: boolean
	ident: string
	miljoe: Array<string>
}

export const PersonMiljoeinfo = ({ bankIdBruker, ident }: PersonMiljoeinfoProps) => {
	if (!ident) return null

	const state = bankIdBruker
		? useAsync(async () => TpsMessagingApi.getTpsPersonInfo(ident, 'q1'), [])
		: useAsync(async () => TpsMessagingApi.getTpsPersonInfoAllEnvs(ident), [])

	return (
		<div>
			<SubOverskrift label="Opprettet i miljøer" iconKind="visTpsData" />
			{state.loading && <Loading label="Laster miljøer" />}
			{/* @ts-ignore */}
			{state.value && <TpsDataVisning data={state.value.data} />}
			{state.value && (
				<p>
					<i>
						Hold pekeren over et miljø for å se dataene som finnes på denne personen i TPS for det
						aktuelle miljøet.
					</i>
				</p>
			)}
		</div>
	)
}
