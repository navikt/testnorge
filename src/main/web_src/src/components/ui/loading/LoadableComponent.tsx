import React, { useEffect, useState } from 'react'
import Loading from './Loading'
import { DollyErrorAlert } from './DollyErrorAlert'

interface LoadableComponent {
	onFetch: any
	renderOnError?: (error: Error) => JSX.Element
	render: (data: any) => JSX.Element
}

const LoadableComponent = ({ onFetch, render, renderOnError }: LoadableComponent) => {
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState()
	const [data, setData] = useState()
	useEffect(() => {
		onFetch()
			.then((response: any) => {
				setData(response)
				setLoading(false)
			})
			.catch((error: Error) => {
				console.error(error.stack)
				setError(error)
				setLoading(false)
			})
	}, [])

	if (error) {
		return renderOnError ? renderOnError(error) : <DollyErrorAlert error={error} />
	}

	if (loading) {
		return <Loading />
	}

	return render(data)
}
export default LoadableComponent
