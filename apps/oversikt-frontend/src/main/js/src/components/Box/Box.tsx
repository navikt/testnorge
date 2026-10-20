import React, { useState } from 'react'
import styled from 'styled-components'
import { NotFoundError } from '@navikt/dolly-lib'
import { InlineMessage } from '@navikt/ds-react'

type Submit<T> = () => Promise<void | T>

type Props<T> = {
	header: string
	onRender: (data: { onSubmit?: Submit<T>; value?: T | null; loading: boolean }) => React.ReactNode
	onSubmit?: Submit<T>
}

const StyledBox = styled.div`
	inline-size: min(100%, 25rem);
	min-block-size: 22.5rem;
	display: flex;
	flex-direction: column;
	gap: var(--ax-space-16);
	padding: var(--ax-space-16);
	border: 1px solid var(--ax-border-neutral-subtle);
	border-radius: var(--ax-radius-16);
	background: var(--ax-bg-raised);
`

const Header = styled.h2`
	margin: 0;
`

const Content = styled.div`
	display: flex;
	flex: 1 1 auto;
	flex-direction: column;
	gap: var(--ax-space-12);

	table {
		border-collapse: collapse;
	}

	td {
		padding-inline-end: var(--ax-space-8);
		padding-block-end: var(--ax-space-4);
		text-align: left;
		vertical-align: top;
	}

	textarea {
		inline-size: 100%;
		max-inline-size: 100%;
		padding: var(--ax-space-12);
		border: 1px solid var(--ax-border-neutral-subtle);
		border-radius: var(--ax-radius-8);
		background: var(--ax-bg-input);
		color: var(--ax-text-neutral);
		opacity: 1;
	}
`

const Messages = styled.div`
	display: flex;
	flex-direction: column;
	gap: var(--ax-space-8);
	margin-top: auto;
`

const Box = <T extends unknown>({ onRender, header, onSubmit }: Props<T>) => {
	const [notFound, setNotFound] = useState<boolean>(false)
	const [error, setError] = useState<boolean>(false)
	const [success, setSuccess] = useState<boolean>(false)
	const [loading, setLoading] = useState<boolean>(false)
	const [value, setValue] = useState<T | null>(null)

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
		: undefined

	return (
		<StyledBox>
			<Header>{header}</Header>
			<Content>{onRender({ onSubmit: submit, value, loading })}</Content>
			<Messages>
				{notFound && (
					<InlineMessage role="alert" size="small" status="warning">
						Ikke funnet.
					</InlineMessage>
				)}
				{error && (
					<InlineMessage role="alert" size="small" status="error">
						Noe gikk galt.
					</InlineMessage>
				)}
				{success && (
					<InlineMessage role="status" size="small" status="success">
						Success!
					</InlineMessage>
				)}
			</Messages>
		</StyledBox>
	)
}

Box.displayName = 'Box'

export default Box
