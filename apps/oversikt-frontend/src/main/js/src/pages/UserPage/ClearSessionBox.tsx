import React from 'react'
import { Box } from '@/components/Box'
import SessionService from '@/services/SessionService'
import styled from 'styled-components'
import { Fareknapp, Hovedknapp } from 'nav-frontend-knapper'

const StyledFareknapp = styled(Fareknapp)`
	margin: 5px 0;
`
const ClearSessionBox = () => (
	<Box
		onSubmit={() => SessionService.clear()}
		header="Fjern session"
		onRender={({ onSubmit, loading }) => (
			<>
				<StyledFareknapp spinner={loading} onClick={onSubmit}>
					Fjern
				</StyledFareknapp>
			</>
		)}
	/>
)

ClearSessionBox.displayName = 'ClearSessionBox'

export default ClearSessionBox
