import React, { useEffect, useState } from 'react'
import { NotFoundError } from '@navikt/dolly-lib'
import { InlineMessage, Loader } from '@navikt/ds-react'

type Props<T> = {
	onFetch: () => Promise<T>
	render: (value: T) => React.ReactNode
	onNotFound?: React.ReactNode
}

const isNotFoundError = (error: unknown) =>
	error instanceof NotFoundError ||
	(error instanceof Error && error.name === NotFoundError.name) ||
	(typeof error === 'object' &&
		error !== null &&
		'name' in error &&
		error.name === NotFoundError.name)

export default function AsyncContent<T>({ onFetch, render, onNotFound }: Props<T>) {
	const [loading, setLoading] = useState(true)
	const [value, setValue] = useState<T | null>(null)
	const [notFound, setNotFound] = useState(false)
	const [error, setError] = useState(false)

	useEffect(() => {
		let isMounted = true

		setLoading(true)
		setNotFound(false)
		setError(false)

		onFetch()
			.then((response) => {
				if (isMounted) {
					setValue(response)
				}
			})
			.catch((reason) => {
				if (!isMounted) {
					return
				}

				if (isNotFoundError(reason)) {
					setNotFound(true)
				} else {
					setError(true)
				}
			})
			.finally(() => {
				if (isMounted) {
					setLoading(false)
				}
			})

		return () => {
			isMounted = false
		}
	}, [onFetch])

	if (notFound) {
		return (
			onNotFound ?? (
				<InlineMessage role="alert" size="small" status="warning">
					Ikke funnet.
				</InlineMessage>
			)
		)
	}

	if (error) {
		return (
			<InlineMessage role="alert" size="small" status="error">
				Noe gikk galt.
			</InlineMessage>
		)
	}

	if (loading) {
		return <Loader title="Laster innhold" />
	}

	if (value === null) {
		return null
	}

	return render(value)
}
