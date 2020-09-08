import React, { useState, useEffect } from 'react'
import Loading from './Loading'

interface LoadableComponent {
	onFetch: any
	render: (data: any, feilmelding: Feilmelding) => JSX.Element
}

export type Feilmelding = {
	feilmelding?: string[]
	feilDetaljert?: string[]
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
				setData(error)
				setError(error)
				setLoading(false)
			})
	}, [])

	if (loading) {
		return <Loading />
	}
	const feil: Feilmelding = error
		? {
				feilmelding: [
					'Noe gikk galt med henting og visning av dette elementet, kontakt Team Dolly'
				],
				feilDetaljert: [error.toString()]
		  }
		: null

	return render(data, feil)
}
export default LoadableComponent
