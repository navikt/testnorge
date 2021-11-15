import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

export const Adressevisning = ({ data }) => {
	const {
		gyldigFraOgMed,
		gyldigTilOgMed,
		matrikkeladresse,
		ukjentBosted,
		vegadresse,
		utenlandskAdresse,
	} = data

	if (vegadresse) {
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

	if (utenlandskAdresse) {
		return (
			<>
				<TitleValue title="Gatenavn og husnummer" value={utenlandskAdresse.adressenavnNummer} />
				<TitleValue title="Postboksnummer og -navn" value={utenlandskAdresse.postboksNummerNavn} />
				<TitleValue title="Postkode" value={utenlandskAdresse.postkode} />
				<TitleValue title="By eller sted" value={utenlandskAdresse.bySted} />
				<TitleValue
					title="Land"
					value={utenlandskAdresse.landkode}
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
				/>
				<TitleValue title="Bygg-/leilighetsinfo" value={utenlandskAdresse.bygningEtasjeLeilighet} />
				<TitleValue
					title="Region/distrikt/område"
					value={utenlandskAdresse.regionDistriktOmraade}
				/>
				<TitleValue title="Gyldig fra" value={Formatters.formatDate(gyldigFraOgMed)} />
				<TitleValue title="Gyldig til" value={Formatters.formatDate(gyldigTilOgMed)} />
			</>
		)
	}
}

export const PdlBoadresse = ({ data }) => {
	if (!data) return null

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<DollyFieldArray data={data} header="" nested>
					{(enhet) => <Adressevisning data={enhet} />}
				</DollyFieldArray>
			</div>
		</>
	)
}
