import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import Search from '@/pages/UserPage/Search'
import BrukerService from '@/services/BrukerService'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard/lib/Component'
import { Button } from '@navikt/ds-react'
// @ts-ignore

const AccessTokenTextArea = styled.textarea`
	width: 100%;
	min-height: 5rem;
	text-align: left;
	resize: none;
	padding: var(--ax-space-12);
	border: 1px solid var(--ax-border-neutral-subtle);
	border-radius: var(--ax-radius-8);
	background: var(--ax-bg-input);
	color: var(--ax-text-neutral);
	opacity: 1;
`

const CopyTokenButton = styled(Button)`
	align-self: flex-start;
`

const LoginBox = () => {
	const [id, setId] = useState('')
	return (
		<Box
			onSubmit={() => BrukerService.getToken(id)}
			header="Login"
			onRender={({ onSubmit, value, loading }) => (
				<>
					<AccessTokenTextArea disabled={true} value={value ?? ''} />
					<CopyToClipboard text={value ?? ''}>
						<CopyTokenButton disabled={!value}>Copy</CopyTokenButton>
					</CopyToClipboard>
					<Search
						onBlur={(event) => setId(event.target.value)}
						onSubmit={onSubmit}
						loading={loading}
						texts={{ label: 'Logg inn med Id:', button: 'Login' }}
					/>
				</>
			)}
		/>
	)
}

LoginBox.displayName = 'LoginBox'

export default LoginBox
