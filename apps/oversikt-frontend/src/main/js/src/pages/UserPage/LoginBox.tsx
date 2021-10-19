import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import { Hovedknapp, Knapp } from 'nav-frontend-knapper'
import Search from '@/pages/UserPage/Search'
import BrukerService from '@/services/BrukerService'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard/lib/Component'
// @ts-ignore

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

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
	const [token, setToken] = useState('')
	const onSubmit = () => {
		setToken('')
		return BrukerService.getToken(id).then((value) => setToken(value))
	}

	return (
		<Box header="Login">
			<AccessTokenTextArea disabled={true} value={token} />
			<CopyToClipboard text={token}>
				<CopyTokenKnapp>Copy</CopyTokenKnapp>
			</CopyToClipboard>
			<Search
				onBlur={(event) => setId(event.target.value)}
				onSubmit={onSubmit}
				texts={{ label: 'Logg inn med Id:', button: 'Login' }}
			/>
		</Box>
	)
}

LoginBox.displayName = 'LoginBox'

export default LoginBox
