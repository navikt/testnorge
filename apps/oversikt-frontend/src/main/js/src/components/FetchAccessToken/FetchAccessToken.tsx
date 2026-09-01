import React, { useState } from 'react'
import TokenService from '@/services/TokenService'
import styled from 'styled-components'
import { NotFoundError } from '@navikt/dolly-lib'

import OrganisasjonService from '@/services/OrganisasjonService'
import { Button, Checkbox, CopyButton, InlineMessage, TextField } from '@navikt/ds-react'

type Props = {
	scope: string
	labels?: {
		header?: string
		subHeader?: string
		description?: string
	}
}

const ButtonGroup = styled.div`
	display: flex;
	justify-content: center;
	gap: var(--ax-space-12);
	flex-wrap: wrap;

	@media (max-width: 767px) {
		justify-content: stretch;

		> * {
			flex: 1 1 100%;
		}
	}
`

const FetchAccessTokenContainer = styled.div`
	inline-size: 100%;
	max-inline-size: 31.25rem;
	display: flex;
	flex-direction: column;
	gap: var(--ax-space-16);
`

const HeaderBlock = styled.div`
	display: flex;
	flex-direction: column;
	gap: var(--ax-space-8);

	h1,
	h2,
	p {
		margin: 0;
	}
`

const StyledInput = styled(TextField)`
	&& {
		width: 100%;
	}
`

const AccessTokenTextArea = styled.textarea`
	inline-size: 100%;
	min-block-size: 12.5rem;
	text-align: left;
	resize: vertical;
	padding: var(--ax-space-12);
	border: 1px solid var(--ax-border-neutral-subtle);
	border-radius: var(--ax-radius-8);
	background: var(--ax-bg-input);
	color: var(--ax-text-neutral);
	opacity: 1;
`

const StyledCheckbox = styled(Checkbox)`
	align-self: flex-start;
`

const isNotFoundError = (error: unknown) =>
	error instanceof NotFoundError ||
	(error instanceof Error && error.name === NotFoundError.name) ||
	(typeof error === 'object' &&
		error !== null &&
		'name' in error &&
		error.name === NotFoundError.name)

export default ({ labels = {}, scope }: Props) => {
	const [accessToken, setAccessToken] = useState<string | null>(null)
	const [clientCredentials, setClientCredentials] = useState(false)
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState<unknown>(null)

	const onGetTokenFromScope = (scope: string) => {
		const parts = scope.split('.')

		setLoading(true)
		setError(null)
		setAccessToken(null)
		TokenService.fetchToken(
			{
				cluster: parts[0],
				namespace: parts[1],
				name: parts[2],
			},
			clientCredentials,
		)
			.then((response) => {
				setAccessToken(response.token)
				setLoading(false)
			})
			.catch((error) => {
				setError(error)
				setLoading(false)
			})
	}

	const onClick = () => onGetTokenFromScope(scope)

	const getError = () => {
		if (isNotFoundError(error)) {
			return (
				<InlineMessage role="alert" size="small" status="warning">
					Token ikke funnet.
				</InlineMessage>
			)
		} else if (error) {
			return (
				<InlineMessage role="alert" size="small" status="error">
					Noe gikk galt. Prøv på nytt.
				</InlineMessage>
			)
		}
		return null
	}

	return (
		<FetchAccessTokenContainer>
			<HeaderBlock>
				{labels.header && <h1>{labels.header}</h1>}
				{labels.subHeader && <h2>{labels.subHeader}</h2>}
				{labels.description && <p>{labels.description}</p>}
			</HeaderBlock>
			<AccessTokenTextArea
				disabled={true}
				value={loading ? 'Laster token...' : accessToken ? accessToken : ''}
			/>
			{getError()}
			<StyledCheckbox
				name="client-credentials-radio"
				checked={clientCredentials}
				onChange={(event) => setClientCredentials(event.target.checked)}
			>
				Client credentials
			</StyledCheckbox>
			<ButtonGroup>
				<Button disabled={loading} onClick={onClick}>
					Hent token
				</Button>
				<CopyButton
					activeText="Kopiert"
					copyText={accessToken ?? ''}
					disabled={loading || !accessToken}
					text="Kopier"
				/>
			</ButtonGroup>
			<StyledInput
				label="Orgnummer (midlertidig)"
				type="text"
				onBlur={(event) =>
					event.target.value && OrganisasjonService.setOrganisasjonsnummer(event.target.value)
				}
			/>
		</FetchAccessTokenContainer>
	)
}
