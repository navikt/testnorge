import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import { Hovedknapp, Knapp } from 'nav-frontend-knapper'
import Search from '@/pages/UserPage/Search'
import BrukerService from '@/services/BrukerService'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard/lib/Component'
// @ts-ignore

const AccessTokenTextArea = styled.textarea`
	width: 100%;
	min-height: 80px;
	text-align: left;
	resize: none;
	background: white;
`

const CopyTokenKnapp = styled(Knapp)`
	margin: 10px 5px;
`

const LoginBox = () => {
	const [id, setId] = useState('')
	return (
		<Box
			onSubmit={() => BrukerService.getToken(id)}
			header="Login"
			onRender={({ onSubmit, value, loading }) => (
				<>
					<AccessTokenTextArea disabled={true} value={value} />
					<CopyToClipboard text={value}>
						<CopyTokenKnapp>Copy</CopyTokenKnapp>
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
