import React, { useState } from 'react'
import Button from '@/components/ui/button/Button'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyApi } from '@/service/Api'

export const EndreMalnavn = ({ malNavn, id, bestilling, avsluttRedigering }) => {
	const [nyttMalnavn, setMalnavn] = useState(malNavn)

	const erOrganisasjon = bestilling?.organisasjon

	const lagreEndring = (nyttMalnavn, id) => {
		erOrganisasjon
			? DollyApi.endreMalNavnOrganisasjon(id, nyttMalnavn).then(() => avsluttRedigering(id))
			: DollyApi.endreMalNavn(id, nyttMalnavn).then(() => avsluttRedigering(id))
	}

	return (
		<ErrorBoundary>
			<div className="endreMalnavn">
				<TextInput
					name="malnavn"
					value={nyttMalnavn}
					onChange={(e) => setMalnavn(e.target.value)}
					className="navnInput"
				/>
				<Button className="lagre" onClick={() => lagreEndring(nyttMalnavn, id)}>
					Lagre
				</Button>
			</div>
		</ErrorBoundary>
	)
}
