import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Adressevisning = ({ data }) => {
	const { gyldigFraOgMed, gyldigTilOgMed, matrikkeladresse, ukjentBosted, vegadresse } = data

	return (
		<>
			<TitleValue title="Matrikkeladresse" value={matrikkeladresse} />
			<TitleValue title="Adressenavn" value={vegadresse.adressenavn} />
			<TitleValue title="Bruksenhetsnummer" value={vegadresse.bruksenhetsnummer} />
			<TitleValue title="Husbokstav" value={vegadresse.husbokstav} />
			<TitleValue title="Husnummer" value={vegadresse.husnummer} />
			<TitleValue title="Kommune" value={vegadresse.kommunenummer} />
			<TitleValue title="Gyldig fra" value={Formatters.formatDate(gyldigFraOgMed)} />
			<TitleValue title="Gyldig til" value={Formatters.formatDate(gyldigTilOgMed)} />
			<TitleValue title="Ukjent bosted" value={ukjentBosted} />
		</>
	)
}

export const PdlBoadresse = ({ data }) => {
	if (!data) return null

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<Adressevisning data={data} />
			</div>
		</>
	)
}
