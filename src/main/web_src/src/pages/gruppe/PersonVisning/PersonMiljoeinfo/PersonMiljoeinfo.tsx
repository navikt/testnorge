import React from 'react'
import { useAsync } from 'react-use'
import { TpsfApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import { TpsDataVisning } from './TpsDataVisning'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

type PersonMiljoeinfo = {
	ident: string
	miljoe: Array<string>
}

export const PersonMiljoeinfo = ({ ident, miljoe }: PersonMiljoeinfo) => {
	if (!ident) return null

	const request = {
		ident: ident,
		miljoe: miljoe
	}

	const state = useAsync(async () => {
		const response = await TpsfApi.hentTpsInnhold(request)
		return response
	}, [])

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
