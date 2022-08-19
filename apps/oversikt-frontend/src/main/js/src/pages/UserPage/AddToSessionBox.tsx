import React, { useState } from 'react'
import { Box } from '@/components/Box'
import SessionService from '@/services/SessionService'
import styled from 'styled-components'
import { InputFormItem, Knapp } from '@navikt/dolly-komponenter/lib'

const StyledHovedknapp = styled(Knapp)`
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
					<InputFormItem
						label="Orgnummer"
						type="text"
						onBlur={(event) => setOrgnummer(event.target.value)}
					/>
					<StyledHovedknapp loading={loading} onClick={onSubmit}>
						Velg
					</StyledHovedknapp>
				</>
			)}
		/>
	)
}

AddToSessionBox.displayName = 'AddToSessionBox'

export default AddToSessionBox
