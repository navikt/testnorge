import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { MatrikkeladresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

type MatrikkeladresseTypes = {
	matrikkeladresse?: MatrikkeladresseData
}

export const Matrikkeladresse = ({ matrikkeladresse }: MatrikkeladresseTypes) => {
	if (!matrikkeladresse) {
		return null
	}

	if (isEmpty(matrikkeladresse, ['matrikkeladresseType'])) {
		return <TitleValue title="Matrikkeladresse" value="Ingen verdier satt" />
	}

	return (
		<>
			<TitleValue title="Gårdsnummer" value={matrikkeladresse.gaardsnummer} />
			<TitleValue title="Bruksnummer" value={matrikkeladresse.bruksnummer} />
			<TitleValue title="Bruksenhetsnummer" value={matrikkeladresse.bruksenhetsnummer} />
			<TitleValue title="Tilleggsnavn" value={matrikkeladresse.tilleggsnavn} />
			<TitleValue title="Postnummer" value={matrikkeladresse.postnummer} />
			<TitleValue title="Kommunenummer" value={matrikkeladresse.kommunenummer} />
		</>
	)
}
