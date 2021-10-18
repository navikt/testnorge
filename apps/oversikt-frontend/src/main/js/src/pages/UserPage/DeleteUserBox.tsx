import { Box } from '@/components/Box'
import React from 'react'
import styled from 'styled-components'
import { Fareknapp } from 'nav-frontend-knapper'

const StyledFareknapp = styled(Fareknapp)`
	margin: 5px 0;
`

const DeleteUserBox = () => (
	<Box header="Slett bruker">
		<StyledFareknapp>Slett</StyledFareknapp>
	</Box>
)

DeleteUserBox.displayName = 'DeleteUserBox'

export default DeleteUserBox
