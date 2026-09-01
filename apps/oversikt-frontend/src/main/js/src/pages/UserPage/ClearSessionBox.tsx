import React from 'react'
import { Box } from '@/components/Box'
import SessionService from '@/services/SessionService'
import { Button } from '@navikt/ds-react'
const ClearSessionBox = () => (
	<Box
		onSubmit={() => SessionService.clear()}
		header="Fjern session"
		onRender={({ onSubmit, loading }) => (
			<>
				<Button data-color="danger" loading={loading} onClick={onSubmit}>
					Fjern
				</Button>
			</>
		)}
	/>
)

ClearSessionBox.displayName = 'ClearSessionBox'

export default ClearSessionBox
