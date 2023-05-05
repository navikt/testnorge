import React, { useState } from 'react'
import Button from '@/components/ui/button/Button'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyApi } from '@/service/Api'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'

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
					data-cy={CypressSelector.INPUT_MINSIDE_ENDRE_MALNAVN}
					name="malnavn"
					value={nyttMalnavn}
					onChange={(e) => setMalnavn(e.target.value)}
					className="navnInput"
				/>
				<Button
					data-cy={CypressSelector.BUTTON_MINSIDE_LAGRE_MALNAVN}
					className="lagre"
					onClick={() => lagreEndring(nyttMalnavn, id)}
				>
					Lagre
				</Button>
			</div>
		</ErrorBoundary>
	)
}
