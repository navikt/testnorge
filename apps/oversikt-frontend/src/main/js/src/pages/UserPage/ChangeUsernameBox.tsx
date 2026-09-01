import React, { useState } from 'react'
import { Box } from '@/components/Box'
import BrukerService from '@/services/BrukerService'
import { Button, TextField } from '@navikt/ds-react'

const ChangeUsernameBox = () => {
	const [username, setUsername] = useState('')
	const [id, setId] = useState('')
	const [jwt, setJwt] = useState('')

	return (
		<Box
			onSubmit={() => {
				if (username && id) {
					return BrukerService.changeBukernavn(id, username, jwt)
				}
				return Promise.resolve()
			}}
			header="Endre brukernavn"
			onRender={({ onSubmit, loading }) => (
				<>
					<TextField label="Id" onBlur={(event) => setId(event.target.value)} />
					<TextField label="Brukernavn" onBlur={(event) => setUsername(event.target.value)} />

					<TextField label="Jwt" onBlur={(event) => setJwt(event.target.value)} />
					<Button loading={loading} onClick={onSubmit}>
						Endre
					</Button>
				</>
			)}
		/>
	)
}

ChangeUsernameBox.displayName = 'ChangeUsernameBox'

export default ChangeUsernameBox
