import React, { useState } from 'react'
import { Box } from '@/components/Box'
import { Input } from 'nav-frontend-skjema'
import OrganisasjonService from '@/services/OrganisasjonService'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

const AddToSessionBox = () => {
	const [orgnummer, setOrgnummer] = useState('')
	return (
		<Box header="Legg til organisasjon til sessionen">
			<Input label="Orgnummer" type="text" onBlur={(event) => setOrgnummer(event.target.value)} />
			<StyledHovedknapp onClick={() => OrganisasjonService.addToSession(orgnummer)}>
				Velg
			</StyledHovedknapp>
		</Box>
	)
}

AddToSessionBox.displayName = 'AddToSessionBox'

export default AddToSessionBox
