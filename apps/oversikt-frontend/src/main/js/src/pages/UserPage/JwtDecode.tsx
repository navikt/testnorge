import React, { useState } from 'react'
import { Box } from '@/components/Box'
import { Input } from 'nav-frontend-skjema'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'

const StyledHovedknapp = styled(Hovedknapp)`
	margin-top: 32px;
	margin-left: 5px;
	margin-bottom: 5px;
`

const AccessTokenTextArea = styled.textarea`
	width: 100%;
	min-height: 180px;
	text-align: left;
	resize: none;
	background: white;
`

const StyledInput = styled(Input)`
	width: 100%;
`

const StyledDiv = styled.div`
	display: flex;
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
						<StyledInput label="Jwt" type="text" onBlur={(event) => setJwt(event.target.value)} />
						<StyledHovedknapp onClick={() => setDecoded(parseJwt(jwt))}>Decode</StyledHovedknapp>
					</StyledDiv>
				</>
			)}
		/>
	)
}

JwtDecode.displayName = 'JwtDecode'

export default JwtDecode
