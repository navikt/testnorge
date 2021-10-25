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
	onSubmit: () => Promise<unknown>
	loading?: boolean
	texts: {
		label: string
		button: string
	}
}

const Search = ({ onBlur, onSubmit, loading = false, texts }: Props) => {
	return (
		<>
			<StyledSearch>
				<StyledInput label={texts.label} type="text" onBlur={onBlur} />
				<StyledHovedknapp spinner={loading} disabled={loading} onClick={onSubmit}>
					{texts.button}
				</StyledHovedknapp>
			</StyledSearch>
		</>
	)
}

Search.displayName = 'Search'

export default Search
