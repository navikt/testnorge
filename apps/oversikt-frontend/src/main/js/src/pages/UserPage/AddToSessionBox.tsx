import React, { useState } from 'react'
import { Box } from '@/components/Box'
import SessionService from '@/services/SessionService'
import { Button, TextField } from '@navikt/ds-react'

const AddToSessionBox = () => {
	const [orgnummer, setOrgnummer] = useState('')
	return (
		<Box
			onSubmit={() => SessionService.addToSession(orgnummer)}
			header="Legg til organisasjon til sessionen"
			onRender={({ onSubmit, loading }) => (
				<>
					<TextField label="Orgnummer" onBlur={(event) => setOrgnummer(event.target.value)} />
					<Button loading={loading} onClick={onSubmit}>
						Velg
					</Button>
				</>
			)}
		/>
	)
}

AddToSessionBox.displayName = 'AddToSessionBox'

export default AddToSessionBox
