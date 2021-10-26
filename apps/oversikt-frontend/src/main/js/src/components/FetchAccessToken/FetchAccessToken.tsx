import React, { useState } from 'react'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard/lib/Component'
// @ts-ignore
import ApplicationService from '@/services/ApplicationService'
import TokenService from '@/services/TokenService'
import { Hovedknapp, Knapp } from 'nav-frontend-knapper'
import SessionTimer from '@/components/SessionTimer'
import styled from 'styled-components'
import { ErrorAlertstripe, WarningAlertstripe } from '@navikt/dolly-komponenter'
import { Checkbox } from 'nav-frontend-skjema'
import { NotFoundError } from '@navikt/dolly-lib'
import { Input } from 'nav-frontend-skjema'

import OrganisasjonService from '@/services/OrganisasjonService'

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
`

const FetchAccessToken = styled.div`
	max-width: 500px;
	padding-bottom: 25px;
	text-align: center;
`

const GetToken = styled(Hovedknapp)`
	margin: 10px 5px;
`

const CopyToken = styled(Knapp)`
	margin: 10px 5px;
`

const AccessTokenTextArea = styled.textarea`
	min-height: 200px;
	min-width: 494px;
	text-align: left;
	resize: vertical;
	background: white;
`

const StyledCheckbox = styled(Checkbox)`
	padding: 5px 0px;
`

export default ({ labels = {}, scope }: Props) => {
	const [accessToken, setAccessToken] = useState(null)
	const [clientCredentials, setClientCredentials] = useState(false)
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null)

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
			clientCredentials
		)
			.then((response: any) => {
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
		if (error && error.name === NotFoundError.name) {
			return <WarningAlertstripe label="Token ikke funnet." />
		} else if (error) {
			return <ErrorAlertstripe label="Noe gikk galt. Prøv på nytt." />
		}
		return null
	}

	return (
		<FetchAccessToken>
			{labels.header && <h1>{labels.header}</h1>}
			{labels.subHeader && <h2>{labels.subHeader}</h2>}
			{labels.description && <p>{labels.description}</p>}
			<AccessTokenTextArea
				disabled={true}
				value={loading ? 'Laster token...' : accessToken ? accessToken : ''}
			/>
			<SessionTimer />
			{getError()}
			<StyledCheckbox
				name="client-credentials-radio"
				label="Client credentials?"
				// @ts-ignore
				value={clientCredentials}
				onChange={(event) => setClientCredentials(event.target.checked)}
			/>
			<ButtonGroup>
				<GetToken disabled={loading} onClick={onClick}>
					Hent token
				</GetToken>
				<CopyToClipboard text={accessToken}>
					<CopyToken disabled={loading}>Copy</CopyToken>
				</CopyToClipboard>
			</ButtonGroup>
			<Input
				label="Orgnummer (Midlertidig)"
				type="text"
				onBlur={(event) =>
					event.target.value && OrganisasjonService.setOrganisasjonsnummer(event.target.value)
				}
			/>
		</FetchAccessToken>
	)
}
