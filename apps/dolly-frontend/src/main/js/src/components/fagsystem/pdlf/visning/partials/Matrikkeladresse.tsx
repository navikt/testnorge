import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { MatrikkelAdresse } from '~/service/services/AdresseService'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

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
