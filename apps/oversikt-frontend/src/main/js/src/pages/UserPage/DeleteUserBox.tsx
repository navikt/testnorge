import { Box } from '@/components/Box'
import React from 'react'
import styled from 'styled-components'
import { Fareknapp } from 'nav-frontend-knapper'

const StyledFareknapp = styled(Fareknapp)`
	margin: 5px 0;
`
export default () => (
	<Box header="Slett bruker">
		<StyledFareknapp>Slett</StyledFareknapp>
	</Box>
)
