import React from 'react'
import { Box } from '@/components/Box'
import SessionService from '@/services/SessionService'
import styled from 'styled-components'
import { Knapp } from '@navikt/dolly-komponenter/lib'

const StyledFareknapp = styled(Knapp)`
	margin: 5px 0;
`
const ClearSessionBox = () => (
	<Box
		onSubmit={() => SessionService.clear()}
		header="Fjern session"
		onRender={({ onSubmit, loading }) => (
			<>
				<StyledFareknapp variant={'danger'} loading={loading} onClick={onSubmit}>
					Fjern
				</StyledFareknapp>
			</>
		)}
	/>
)

ClearSessionBox.displayName = 'ClearSessionBox'

export default ClearSessionBox
