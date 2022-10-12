import { Box } from '@/components/Box'
import React, { useState } from 'react'
import styled from 'styled-components'
import BrukerService from '@/services/BrukerService'
import { InputFormItem, Knapp } from '@navikt/dolly-komponenter/lib'

const StyledHovedknapp = styled(Knapp)`
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
					<InputFormItem
						label="Brukernavn"
						type="text"
						onBlur={(event) => setBurkernavn(event.target.value)}
					/>
					<InputFormItem
						label="Orgnummer"
						type="text"
						onBlur={(event) => setOrgnummer(event.target.value)}
					/>
					<StyledHovedknapp loading={loading} onClick={onSubmit}>
						Opprett
					</StyledHovedknapp>
				</>
			)}
		/>
	)
}

CreateUserBox.displayName = 'CreateUserBox'

export default CreateUserBox
