import React from 'react'
import { useAsync } from 'react-use'
import Formatters from '~/utils/DataFormatter'

export default function TilgjengeligeMiljoer({ endepunkt, dollyEnvironments }) {
	if (!endepunkt) return false

	const state = useAsync(async () => {
		const response = await endepunkt()
		return response
	}, [endepunkt])

	let message = 'Laster tilgjengelige miljøer..'

	if (state.value && state.value.data) {
		message = Formatters.arrayToString(filterMiljoe(dollyEnvironments, state.value.data))
	} else if (state.error) {
		message = state.error.message
	}

	return <span>{message}</span>
}

export const filterMiljoe = (dollyMiljoe, utvalgteMiljoer) => {
	if (utvalgteMiljoer === null) return []
	const dollyMiljoeArray = flatDollyMiljoe(dollyMiljoe)

	//Filtrerer bort de miljøene som er tilgjengelige for fagsystemene eller en mal,
	//men ikke Dolly per dags dato
	return utvalgteMiljoer.filter(miljoe => dollyMiljoeArray.includes(miljoe))
}

const flatDollyMiljoe = dollymiljoe => {
	const miljoeArray = []
	Object.values(dollymiljoe).forEach(miljoeKat =>
		miljoeKat.forEach(miljoe => miljoeArray.push(miljoe.id))
	)
	return miljoeArray
}
