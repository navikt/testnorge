import React from 'react'
import { useAsync } from 'react-use'
import Formatters from '~/utils/DataFormatter'

export default function TilgjengeligeMiljoer({ endepunkt }) {
	if (!endepunkt) return false

	const state = useAsync(async () => {
		const response = await endepunkt
		return response
	}, [endepunkt])

	let message = 'laster tilgjengelige miljøer..'

	if (state.value && state.value.data) {
		message = Formatters.arrayToString(state.value.data)
	} else if (state.error) {
		message = state.error.message
	}

	return <span>Tilgjengelige miljø: {message}</span>
}
