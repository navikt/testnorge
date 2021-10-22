import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'
import { Input } from 'nav-frontend-skjema'
import BrukerService from '@/services/BrukerService'

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

const ChangeUsernameBox = () => {
	const [username, setUsername] = useState(null)
	const [id, setId] = useState(null)
	const [jwt, setJwt] = useState(null)

	return (
		<Box
			onSubmit={() => {
				if (username && id) {
					return BrukerService.changeBukernavn(id, username, jwt)
				}
			}}
			header="Endre brukernavn"
			onRender={({ onSubmit, loading }) => (
				<>
					<Input label="Id" type="text" onBlur={(event) => setId(event.target.value)} />
					<Input
						label="Brukernavn"
						type="text"
						onBlur={(event) => setUsername(event.target.value)}
					/>

					<Input label="Jwt" type="text" onBlur={(event) => setJwt(event.target.value)} />
					<StyledHovedknapp spinner={loading} onClick={onSubmit}>
						Endre
					</StyledHovedknapp>
				</>
			)}
		/>
	)
}

ChangeUsernameBox.displayName = 'ChangeUsernameBox'

export default ChangeUsernameBox
