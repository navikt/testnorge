import React, { FocusEventHandler } from 'react'
import styled from 'styled-components'
import { Button, TextField } from '@navikt/ds-react'

const StyledPrimaryButton = styled(Button)`
	flex-shrink: 0;
`

const StyledSearch = styled.div`
	display: flex;
	align-items: flex-end;
	gap: var(--ax-space-8);

	@media (max-width: 767px) {
		flex-direction: column;
		align-items: stretch;
	}
`

const StyledInput = styled(TextField)`
	flex: 1 1 auto;
`

const StyledButtonGroup = styled.div`
	display: flex;

	@media (max-width: 767px) {
		width: 100%;
	}
`

type Props = {
	onBlur: FocusEventHandler<HTMLInputElement>
	onSubmit?: () => Promise<unknown> | void
	loading?: boolean
	texts: {
		label: string
		button: string
	}
}

const Search = ({ onBlur, onSubmit, loading = false, texts }: Props) => {
	return (
		<StyledSearch>
			<StyledInput label={texts.label} onBlur={onBlur} />
			<StyledButtonGroup>
				<StyledPrimaryButton
					loading={loading}
					disabled={loading || !onSubmit}
					onClick={() => onSubmit?.()}
				>
					{texts.button}
				</StyledPrimaryButton>
			</StyledButtonGroup>
		</StyledSearch>
	)
}

Search.displayName = 'Search'

export default Search
