import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Adresse } from '~/service/services/AdresseService'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

interface VegadresseValues {
	adresse: {
		vegadresse: Adresse
		angittFlyttedato: string
		gyldigFraOgMed: string
		gyldigTilOgMed: string
	}
	idx: number
}

export const Vegadresse = ({ adresse, idx }: VegadresseValues) => {
	const {
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
				<TitleValue title="Adressenavn" value={adressenavn} />
				<TitleValue title="Tilleggsnavn" value={tilleggsnavn} />
				<TitleValue title="Husnummer" value={husnummer} />
				<TitleValue title="Husbokstav" value={husbokstav} />
				<TitleValue title="Bruksenhetsnummer" value={bruksenhetsnummer} />
				<TitleValue title="Postnummer">
					{postnummer && (
						<KodeverkConnector navn="Postnummer" value={postnummer}>
							{(v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : postnummer}</span>
							)}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue title="Kommunenummer">
					{kommunenummer && (
						<KodeverkConnector navn="Kommuner" value={kommunenummer}>
							{(v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : kommunenummer}</span>
							)}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue title="Angitt flyttedato" value={Formatters.formatDate(angittFlyttedato)} />
				<TitleValue title="Gyldig fra og med" value={Formatters.formatDate(gyldigFraOgMed)} />
				<TitleValue title="Gyldig til og med" value={Formatters.formatDate(gyldigTilOgMed)} />
			</div>
		</>
	)
}
