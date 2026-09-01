import { Box } from '@/components/Box'
import React, { useState } from 'react'
import BrukerService from '@/services/BrukerService'
import { Button, TextField } from '@navikt/ds-react'

const DeleteUserBox = () => {
	const [id, setId] = useState('')
	const [jwt, setJwt] = useState('')

	return (
		<Box
			onSubmit={() => BrukerService.deleteBruker(id, jwt)}
			header="Slett bruker"
			onRender={({ onSubmit, loading }) => (
				<>
					<TextField label="Id" onBlur={(event) => setId(event.target.value)} />
					<TextField label="Jwt" onBlur={(event) => setJwt(event.target.value)} />
					<Button data-color="danger" loading={loading} onClick={onSubmit}>
						Slett
					</Button>
				</>
			)}
		/>
	)
}

DeleteUserBox.displayName = 'DeleteUserBox'

export default DeleteUserBox
