import React, { useEffect, useState } from 'react'
import Loading from './Loading'
import { ErrorComponent } from './ErrorComponent'

interface LoadableComponent {
	onFetch: any
	renderOnError?: (error: any) => JSX.Element
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
			.catch(error => {
				setError(error)
				setLoading(false)
			})
	}, [])

	if (error) {
		return renderOnError ? (
			renderOnError(error.toString())
		) : (
			<ErrorComponent errorMessage={error.toString()} />
		)
	}

	if (loading) {
		return <Loading />
	}

	return render(data)
}
export default LoadableComponent
