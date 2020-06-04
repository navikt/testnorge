import React, { useState, useEffect } from 'react'
import Loading from './Loading'

interface LoadableComponent {
	onFetch: any
	render: (data: any, feilmelding: Feilmelding) => JSX.Element
}

export type Feilmelding = {
	feilmelding?: string
}

const LoadableComponent = ({ onFetch, render }: LoadableComponent) => {
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState()
	const [data, setData] = useState()
	useEffect(() => {
		onFetch()
			.then((response: any) => {
				setData(response)
				setLoading(false)
			})
			.catch((error: any) => {
				setError(error)
				setLoading(false)
			})
	}, [])

	if (loading) {
		return <Loading />
	}
	const feilmelding: Feilmelding = error
		? { feilmelding: 'Noe gikk galt ved henting av valg: ' + error }
		: null

	return render(data, feilmelding)
}
export default LoadableComponent
