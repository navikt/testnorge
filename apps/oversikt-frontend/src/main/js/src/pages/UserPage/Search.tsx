import { InputFormItem, Knapp } from '@navikt/dolly-komponenter/lib'
import React, { FocusEventHandler } from 'react'
import styled from 'styled-components'

const StyledHovedknapp = styled(Knapp)`
	margin-top: 32px;
	margin-left: 5px;
	margin-bottom: 5px;
`

const StyledSearch = styled.div`
	display: flex;
`

const StyledInput = styled(InputFormItem)`
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
				<StyledHovedknapp loading={loading} disabled={loading} onClick={onSubmit}>
					{texts.button}
				</StyledHovedknapp>
			</StyledSearch>
		</>
	)
}

Search.displayName = 'Search'

export default Search
