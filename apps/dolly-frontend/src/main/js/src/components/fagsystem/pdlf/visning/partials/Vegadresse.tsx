import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Vegadresse = ({ adresse, idx }) => {
	const {
		adressekode,
		adressenavn,
		tilleggsnavn,
		bruksenhetsnummer,
		husbokstav,
		husnummer,
		kommunenummer,
		postnummer,
	} = adresse.vegadresse
	const { angittFlyttedato, gyldigFraOgMed, gyldigTilOgMed } = adresse

	return (
		<>
			<h4 style={{ marginTop: '0px' }}>Vegadresse</h4>
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Adressekode" value={adressekode} />
				<TitleValue title="Adressenavn" value={adressenavn} />
				<TitleValue title="Tilleggsnavn" value={tilleggsnavn} />
				<TitleValue title="Bruksenhetsnummer" value={bruksenhetsnummer} />
				<TitleValue title="Husnummer" value={husnummer} />
				<TitleValue title="Husbokstav" value={husbokstav} />
				<TitleValue title="Postnummer" value={postnummer} />
				<TitleValue title="Kommunenummer" value={kommunenummer} />
				<TitleValue title="Angitt flyttedato" value={Formatters.formatDate(angittFlyttedato)} />
				<TitleValue title="Gyldig fra og med" value={Formatters.formatDate(gyldigFraOgMed)} />
				<TitleValue title="Gyldig til og med" value={Formatters.formatDate(gyldigTilOgMed)} />
			</div>
		</>
	)
}
