import { Box } from '@/components/Box'
import React, { useState } from 'react'
import styled from 'styled-components'
import { Fareknapp } from 'nav-frontend-knapper'
import BrukerService from '@/services/BrukerService'
import { Input } from 'nav-frontend-skjema'

const StyledFareknapp = styled(Fareknapp)`
	margin: 5px 0;
`

const DeleteUserBox = () => {
	const [id, setId] = useState(null)
	const [jwt, setJwt] = useState(null)

	return (
		<Box
			onSubmit={() => BrukerService.deleteBruker(id, jwt)}
			header="Slett bruker"
			onRender={({ onSubmit, loading }) => (
				<>
					<Input label="Id" type="text" onBlur={(event) => setId(event.target.value)} />
					<Input label="Jwt" type="text" onBlur={(event) => setJwt(event.target.value)} />
					<StyledFareknapp spinner={loading} onClick={onSubmit}>
						Slett
					</StyledFareknapp>
				</>
			)}
		/>
	)
}

DeleteUserBox.displayName = 'DeleteUserBox'

export default DeleteUserBox
