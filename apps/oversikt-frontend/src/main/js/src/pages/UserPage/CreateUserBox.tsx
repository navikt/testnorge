import { Box } from '@/components/Box'
import { Input } from 'nav-frontend-skjema'
import React from 'react'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

const CreateUserBox = () => (
	<Box header="Opprett bruker">
		<Input label="Brukernavn" type="text" />
		<Input label="Orgnummer" type="text" />
		<StyledHovedknapp>Opprett</StyledHovedknapp>
	</Box>
)

CreateUserBox.displayName = 'CreateUserBox'

export default CreateUserBox
