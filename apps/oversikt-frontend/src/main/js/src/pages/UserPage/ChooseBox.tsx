import React, { useState } from 'react'
import { Box } from '@/components/Box'
import { Input } from 'nav-frontend-skjema'
import OrganisasjonService from '@/services/OrganisasjonService'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

const ChooseBox = () => {
	const [orgnummer, setOrgnummer] = useState('')
	return (
		<Box header="Velg organisasasjon">
			<Input label="Orgnummer" type="text" onBlur={(event) => setOrgnummer(event.target.value)} />
			<StyledHovedknapp onClick={(event) => OrganisasjonService.setOrganisasjonsnummer(orgnummer)}>
				Velg
			</StyledHovedknapp>
		</Box>
	)
}

ChooseBox.displayName = 'ChooseBox'

export default ChooseBox
