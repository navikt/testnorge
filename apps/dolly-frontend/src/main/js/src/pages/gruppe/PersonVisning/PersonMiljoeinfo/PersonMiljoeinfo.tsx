import React from 'react'
import { useAsync } from 'react-use'
import Loading from '~/components/ui/loading/Loading'
import { TpsDataVisning } from './TpsDataVisning'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TpsMessagingApi, BankkontoApi } from '~/service/Api'

type PersonMiljoeinfoProps = {
	bankIdBruker: boolean
	ident: string
}

export const PersonMiljoeinfo = ({ bankIdBruker, ident }: PersonMiljoeinfoProps) => {
	const state = useAsync(async () => {
		if (ident) {
			return bankIdBruker
				? TpsMessagingApi.getTpsPersonInfo(ident, ['q1'])
				: TpsMessagingApi.getTpsPersonInfoAllEnvs(ident)
		}
	}, [])

	const kontoregisterState = useAsync( async () => {
		if (ident) {
			return BankkontoApi.hentKonto(ident)
		}
	})

	if (!ident) {
		return null
	}

	console.log('kontoregisterState', kontoregisterState, state.value)

	//@ts-ignore
	if (state.value?.data) {
		//@ts-ignore
		state.value.data.map(m => {
			if (m.person) {
				//m.person.bankkontonrUtland = {kontonummer: '234'}
			}

		})
	}

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
