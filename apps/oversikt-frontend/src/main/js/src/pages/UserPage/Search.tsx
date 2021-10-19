import React, { FocusEventHandler, useState } from 'react'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'
import { Input } from 'nav-frontend-skjema'
import { NotFoundError } from '@navikt/dolly-lib'
import { ErrorAlert, WarningAlert } from '@navikt/dolly-komponenter'

const StyledHovedknapp = styled(Hovedknapp)`
	margin-top: 32px;
	margin-left: 5px;
	margin-bottom: 5px;
`

const StyledSearch = styled.div`
	display: flex;
`

const StyledInput = styled(Input)`
	width: 100%;
`

type Props = {
	onBlur: FocusEventHandler<HTMLInputElement>
	onSubmit: (event: React.MouseEvent<HTMLButtonElement>) => Promise<unknown>
	texts: {
		label: string
		button: string
	}
}

const Search = ({ onBlur, onSubmit, texts }: Props) => {
	const [notFound, setNotFound] = useState<boolean>(false)
	const [error, setError] = useState<boolean>(false)
	const [loading, setLoading] = useState<boolean>(false)

	const submit = (event: React.MouseEvent<HTMLButtonElement>) => {
		setError(false)
		setNotFound(false)
		setLoading(true)
		return onSubmit(event)
			.catch((e: Error) => {
				if (e && (e instanceof NotFoundError || e.name == 'NotFoundError')) {
					setNotFound(true)
				} else {
					setError(true)
				}
			})
			.finally(() => setLoading(false))
	}

	return (
		<>
			<StyledSearch>
				<StyledInput label={texts.label} type="text" onBlur={onBlur} />
				<StyledHovedknapp spinner={loading} disabled={loading} onClick={submit}>
					{texts.button}
				</StyledHovedknapp>
			</StyledSearch>
			{notFound && <WarningAlert label="Ikke funnet." />}
			{error && <ErrorAlert label="Noe gikk galt." />}
		</>
	)
}

Search.displayName = 'Search'

export default Search
