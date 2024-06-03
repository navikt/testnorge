import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyApi } from '@/service/Api'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { Button, TextField } from '@navikt/ds-react'

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
				<TextField
					data-testid={CypressSelector.INPUT_MINSIDE_ENDRE_MALNAVN}
					size={'small'}
					label={'Skriv inn nytt malnavn'}
					hideLabel
					value={nyttMalnavn}
					onChange={(e) => setMalnavn(e.target.value)}
					className="navnInput"
				/>
				<Button
					data-testid={CypressSelector.BUTTON_MINSIDE_LAGRE_MALNAVN}
					variant={'primary'}
					size={'small'}
					onClick={() => lagreEndring(nyttMalnavn, id)}
				>
					Lagre
				</Button>
			</div>
		</ErrorBoundary>
	)
}
