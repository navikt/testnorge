import { Box } from '@/components/Box'
import { Input } from 'nav-frontend-skjema'
import React, { useState } from 'react'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'
import BrukerService from '@/services/BrukerService'

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

const CreateUserBox = () => {
	const [brukernavn, setBurkernavn] = useState(null)
	const [orgnummer, setOrgnummer] = useState(null)

	return (
		<Box
			onSubmit={() => BrukerService.createBruker(orgnummer, brukernavn)}
			header="Opprett bruker"
			onRender={({ onSubmit, loading }) => (
				<>
					<Input
						label="Brukernavn"
						type="text"
						onBlur={(event) => setBurkernavn(event.target.value)}
					/>
					<Input
						label="Orgnummer"
						type="text"
						onBlur={(event) => setOrgnummer(event.target.value)}
					/>
					<StyledHovedknapp spinner={loading} onClick={onSubmit}>
						Opprett
					</StyledHovedknapp>
				</>
			)}
		/>
	)
}

CreateUserBox.displayName = 'CreateUserBox'

export default CreateUserBox
