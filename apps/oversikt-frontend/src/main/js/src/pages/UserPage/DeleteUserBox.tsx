import { Box } from '@/components/Box'
import React, { useState } from 'react'
import styled from 'styled-components'
import BrukerService from '@/services/BrukerService'
import { Knapp, InputFormItem } from '@navikt/dolly-komponenter/lib'

const StyledFareknapp = styled(Knapp)`
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
					<InputFormItem label="Id" type="text" onBlur={(event) => setId(event.target.value)} />
					<InputFormItem label="Jwt" type="text" onBlur={(event) => setJwt(event.target.value)} />
					<StyledFareknapp variant={'danger'} loading={loading} onClick={onSubmit}>
						Slett
					</StyledFareknapp>
				</>
			)}
		/>
	)
}

DeleteUserBox.displayName = 'DeleteUserBox'

export default DeleteUserBox
