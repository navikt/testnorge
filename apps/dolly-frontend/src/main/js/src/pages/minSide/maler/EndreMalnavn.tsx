import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyApi } from '@/service/Api'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Button, TextField } from '@navikt/ds-react'
import { MalType } from '@/pages/minSide/maler/Maloversikt'
import { tenorEndrePersonMal } from '@/service/services/templatesearch/TemplateSearch'

interface EndreMalnavnProps {
	malNavn: string
	id: number
	type: string
	avsluttRedigering: (id: number) => void
}

export const EndreMalnavn = ({ malNavn, id, type, avsluttRedigering }: EndreMalnavnProps) => {
	const [nyttMalnavn, setNyttMalnavn] = useState(malNavn)

	const lagreEndring = () => {
		switch (type) {
			case MalType.ORGANISASJON:
				return DollyApi.endreMalNavnOrganisasjon(id, nyttMalnavn).then(() => avsluttRedigering(id))
			case MalType.PERSON:
				return DollyApi.endreMalNavn(id, nyttMalnavn).then(() => avsluttRedigering(id))
			case MalType.TENORSOEK:
				return tenorEndrePersonMal(id, nyttMalnavn).then(() => avsluttRedigering(id))
			default:
				return null
		}
	}

	return (
		<ErrorBoundary>
			<div className="endreMalnavn">
				<TextField
					data-testid={TestComponentSelectors.INPUT_MINSIDE_ENDRE_MALNAVN}
					size={'small'}
					label={'Skriv inn nytt malnavn'}
					hideLabel
					value={nyttMalnavn}
					onChange={(e) => setNyttMalnavn(e.target.value)}
					className="navnInput"
				/>
				<Button
					data-testid={TestComponentSelectors.BUTTON_MINSIDE_LAGRE_MALNAVN}
					variant={'primary'}
					size={'small'}
					onClick={() => lagreEndring()}
				>
					Lagre
				</Button>
			</div>
		</ErrorBoundary>
	)
}
