import React, { useState } from 'react'
import styled from 'styled-components'
import { ErrorAlert, SuccessAlert, WarningAlert } from '@navikt/dolly-komponenter'
import { NotFoundError } from '@navikt/dolly-lib'

type Submit<T> = () => Promise<void | T>

type Props<T> = {
	header: string
	onRender: (data: { onSubmit?: Submit<T>; value?: T; loading: boolean }) => React.ReactNode
	onSubmit?: Submit<T>
}

const StyledBox = styled.div`
	width: 400px;
	height: 360px;
	border: 1px solid;
	margin: 10px;
	padding: 12px;
	border-radius: 25px;
`

const Header = styled.h3`
	margin: 6px 0;
`

const Box = <T extends unknown>({ onRender, header, onSubmit }: Props<T>) => {
	const [notFound, setNotFound] = useState<boolean>(false)
	const [error, setError] = useState<boolean>(false)
	const [success, setSuccess] = useState<boolean>(false)
	const [loading, setLoading] = useState<boolean>(false)
	const [value, setValue] = useState<T>(null)

	const submit = onSubmit
		? () => {
				setValue(null)
				setLoading(true)
				setSuccess(false)
				setError(false)
				setNotFound(false)
				return onSubmit()
					.then((response) => {
						setSuccess(true)
						if (response) {
							setValue(response)
						}
						return response
					})
					.catch((e: Error) => {
						if (e && (e instanceof NotFoundError || e.name == 'NotFoundError')) {
							setNotFound(true)
						} else {
							setError(true)
						}
					})
					.finally(() => setLoading(false))
		  }
		: null

	return (
		<StyledBox>
			<Header>{header}</Header>
			{onRender({ onSubmit: submit, value, loading })}
			{notFound && <WarningAlert label="Ikke funnet." />}
			{error && <ErrorAlert label="Noe gikk galt." />}
			{success && <SuccessAlert label="Success!" />}
		</StyledBox>
	)
}

Box.displayName = 'Box'

export default Box
