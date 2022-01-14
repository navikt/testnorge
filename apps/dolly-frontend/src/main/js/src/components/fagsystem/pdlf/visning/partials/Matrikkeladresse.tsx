import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { MatrikkelAdresse } from '~/service/services/AdresseService'

interface MatrikkeladresseValues {
	adresse: {
		matrikkeladresse: MatrikkelAdresse
		angittFlyttedato: string
		gyldigFraOgMed: string
		gyldigTilOgMed: string
	}
	idx: number
}

export const Matrikkeladresse = ({ adresse, idx }: MatrikkeladresseValues) => {
	const { kommunenummer, gaardsnummer, bruksnummer, postnummer, bruksenhetsnummer, tilleggsnavn } =
		adresse.matrikkeladresse
	const { angittFlyttedato, gyldigFraOgMed, gyldigTilOgMed } = adresse

	return (
		<>
			<h4 style={{ marginTop: '0px' }}>Matrikkeladresse</h4>
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Gårdsnummer" value={gaardsnummer} />
				<TitleValue title="Bruksnummer" value={bruksnummer} />
				<TitleValue title="Bruksenhetsnummer" value={bruksenhetsnummer} />
				<TitleValue title="Tilleggsnavn" value={tilleggsnavn} />
				<TitleValue title="Postnummer" value={postnummer} />
				<TitleValue title="Kommunenummer" value={kommunenummer} />
				<TitleValue title="Angitt flyttedato" value={Formatters.formatDate(angittFlyttedato)} />
				<TitleValue title="Gyldig fra og med" value={Formatters.formatDate(gyldigFraOgMed)} />
				<TitleValue title="Gyldig til og med" value={Formatters.formatDate(gyldigTilOgMed)} />
			</div>
		</>
	)
}
