import React, { useEffect, useState } from 'react'
import Loading from './Loading'

interface LoadableComponentProps {
	onFetch: any
	render: (data: any, feilmelding: Feilmelding) => JSX.Element
	label?: string
}

export type Feilmelding = {
	feilmelding?: string
}

const LoadableComponent = ({ onFetch, render, label }: LoadableComponentProps) => {
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState()
	const [data, setData] = useState()
	useEffect(() => {
		onFetch()
			.then((response: any) => {
				setData(response)
				setLoading(false)
			})
			.catch((e: any) => {
				setError(e)
				setLoading(false)
			})
	}, [])

	if (loading) {
		return <Loading label={label} />
	}
	const feilmelding: Feilmelding = error
		? {
				feilmelding:
					'Noe gikk galt ved henting av valg. Ta kontakt med Team Dolly hvis ikke en refresh av siden hjelper.',
		  }
		: null
	return render(data, feilmelding)
}
export default LoadableComponent
