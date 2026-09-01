import { Box } from '@/components/Box'
import React, { useState } from 'react'
import BrukerService from '@/services/BrukerService'
import { Button, TextField } from '@navikt/ds-react'

const CreateUserBox = () => {
	const [brukernavn, setBurkernavn] = useState('')
	const [orgnummer, setOrgnummer] = useState('')

	return (
		<Box
			onSubmit={() => BrukerService.createBruker(orgnummer, brukernavn)}
			header="Opprett bruker"
			onRender={({ onSubmit, loading }) => (
				<>
					<TextField label="Brukernavn" onBlur={(event) => setBurkernavn(event.target.value)} />
					<TextField label="Orgnummer" onBlur={(event) => setOrgnummer(event.target.value)} />
					<Button loading={loading} onClick={onSubmit}>
						Opprett
					</Button>
				</>
			)}
		/>
	)
}

CreateUserBox.displayName = 'CreateUserBox'

export default CreateUserBox
