import React from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'
import { Input } from 'nav-frontend-skjema'

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

export default () => (
	<Box header="Endre brukernavn">
		<Input label="Brukernavn" type="text" />
		<StyledHovedknapp>Endre</StyledHovedknapp>
	</Box>
)
