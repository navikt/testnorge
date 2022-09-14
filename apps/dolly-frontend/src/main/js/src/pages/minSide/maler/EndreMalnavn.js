import React, { useState } from 'react'
import Button from '~/components/ui/button/Button'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyApi } from '~/service/Api'

export const EndreMalnavn = ({ malInfo, avsluttRedigering }) => {
	const { malNavn, id } = malInfo
	const [nyttMalnavn, setMalnavn] = useState(malNavn)

	const lagreEndring = (nyttMalnavn, id) => {
		DollyApi.endreMalNavn(id, nyttMalnavn).then(() => avsluttRedigering(id))
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
