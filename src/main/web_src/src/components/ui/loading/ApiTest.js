import React from 'react'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

// Eksempel-API. Brukes f.eks. ved behov for å se resultat ved feilet api-kall.
export const api = {
	send: () =>
		new Promise(resolve => {
			setTimeout(() => resolve(), 2000)
		}).then(() => 'Dette er responsen fra APIet'),

	sendMedFeil: () =>
		new Promise((resolve, reject) => {
			setTimeout(
				() =>
					reject(
						'Feilet å hente fra api fordi appen som bidrar med verdier ikke er oppe og kjøre akkurat nå av kjente eller ukjente grunner'
					),
				2000
			)
		})
}

export const FeiletApi = () => {
	return (
		<ErrorBoundary>
			<LoadableComponent onFetch={() => api.sendMedFeil()} render={data => render(data)} />
		</ErrorBoundary>
	)
}
export const SuksessApi = () => {
	return (
		<ErrorBoundary>
			<LoadableComponent onFetch={() => api.send()} render={data => render(data)} />
		</ErrorBoundary>
	)
}
const render = data => <div>{data}</div>
