import React from 'react'
import { useAsync } from 'react-use'
import Loading from '~/components/ui/loading/Loading'
import Formatters from '~/utils/DataFormatter'

export default function TilgjengeligeMiljoer({ endepunkt }) {
	const state = useAsync(
		async () => {
			const response = await endepunkt
			return response
		},
		[endepunkt]
	)

	if (state.loading || !state.value) return <Loading label="laster tilgjengelige miljøer" />

	if (state.error) return <p>{state.error.message}</p>

	return <p>Tilgjengelige miljø: {Formatters.arrayToString(state.value.data)}</p>
}
