import React, { useState } from 'react'
import { Box } from '@/components/Box'
import { Input } from 'nav-frontend-skjema'
import SessionService from '@/services/SessionService'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

const AddToSessionBox = () => {
	const [orgnummer, setOrgnummer] = useState('')
	return (
		<Box
			onSubmit={() => SessionService.addToSession(orgnummer)}
			header="Legg til organisasjon til sessionen"
			onRender={({ onSubmit, loading }) => (
				<>
					<Input
						label="Orgnummer"
						type="text"
						onBlur={(event) => setOrgnummer(event.target.value)}
					/>
					<StyledHovedknapp spinner={loading} onClick={onSubmit}>
						Velg
					</StyledHovedknapp>
				</>
			)}
		/>
	)
}

AddToSessionBox.displayName = 'AddToSessionBox'

export default AddToSessionBox
