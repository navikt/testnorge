import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import { Button, TextField } from '@navikt/ds-react'

const StyledPrimaryButton = styled(Button)`
	flex-shrink: 0;
`

const AccessTokenTextArea = styled.textarea`
	width: 100%;
	min-height: 11.25rem;
	text-align: left;
	resize: none;
	padding: var(--ax-space-12);
	border: 1px solid var(--ax-border-neutral-subtle);
	border-radius: var(--ax-radius-8);
	background: var(--ax-bg-input);
	color: var(--ax-text-neutral);
	opacity: 1;
`

const StyledInput = styled(TextField)`
	flex: 1 1 auto;
`

const StyledDiv = styled.div`
	display: flex;
	align-items: flex-end;
	gap: var(--ax-space-8);

	@media (max-width: 767px) {
		flex-direction: column;
		align-items: stretch;
	}
`

const parseJwt = (token: string) => {
	try {
		return JSON.stringify(JSON.parse(atob(token.split('.')[1])), null, 2)
	} catch (e) {
		return null
	}
}

const JwtDecode = () => {
	const [jwt, setJwt] = useState('')
	const [decoded, setDecoded] = useState('')

	return (
		<Box
			header="Decode jwt"
			onRender={() => (
				<>
					<AccessTokenTextArea disabled={true} value={decoded} />
					<StyledDiv>
						<StyledInput label="Jwt" onBlur={(event) => setJwt(event.target.value)} />
						<StyledPrimaryButton onClick={() => setDecoded(parseJwt(jwt) ?? '')}>
							Decode
						</StyledPrimaryButton>
					</StyledDiv>
				</>
			)}
		/>
	)
}

JwtDecode.displayName = 'JwtDecode'

export default JwtDecode
