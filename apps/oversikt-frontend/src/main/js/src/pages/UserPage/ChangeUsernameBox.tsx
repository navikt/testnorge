import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import BrukerService from '@/services/BrukerService'
import { InputFormItem, Knapp } from '@navikt/dolly-komponenter/lib'

const StyledHovedknapp = styled(Knapp)`
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
					<InputFormItem label="Id" type="text" onBlur={(event) => setId(event.target.value)} />
					<InputFormItem
						label="Brukernavn"
						type="text"
						onBlur={(event) => setUsername(event.target.value)}
					/>

					<InputFormItem label="Jwt" type="text" onBlur={(event) => setJwt(event.target.value)} />
					<StyledHovedknapp loading={loading} onClick={onSubmit}>
						Endre
					</StyledHovedknapp>
				</>
			)}
		/>
	)
}

ChangeUsernameBox.displayName = 'ChangeUsernameBox'

export default ChangeUsernameBox
