import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { VegadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

type VegadresseTypes = {
	vegadresse?: VegadresseData
}

export const Vegadresse = ({ vegadresse }: VegadresseTypes) => {
	if (!vegadresse) {
		return null
	}

	if (isEmpty(vegadresse, ['vegadresseType'])) {
		return <TitleValue title="Vegadresse" value="Ingen verdier satt" />
	}

	return (
		<>
			<TitleValue title="Adressekode" value={vegadresse.adressekode} />
			<TitleValue title="Adressenavn" value={vegadresse.adressenavn} />
			<TitleValue title="Tilleggsnavn" value={vegadresse.tilleggsnavn} />
			<TitleValue title="Bruksenhetsnummer" value={vegadresse.bruksenhetsnummer} />
			<TitleValue title="Husnummer" value={vegadresse.husnummer} />
			<TitleValue title="Husbokstav" value={vegadresse.husbokstav} />
			<TitleValue title="Postnummer" value={vegadresse.postnummer} />
			<TitleValue title="Bydelsnummer" value={vegadresse.bydelsnummer} />
			<TitleValue title="Kommunenummer" value={vegadresse.kommunenummer} />
		</>
	)
}
