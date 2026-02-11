import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { UtenlandskAdresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { AdresseKodeverk } from '@/config/kodeverk'

type UtenlandskAdresseTypes = {
	utenlandskAdresse?: UtenlandskAdresseData
}

export const UtenlandskAdresse = ({ utenlandskAdresse }: UtenlandskAdresseTypes) => {
	if (!utenlandskAdresse) {
		return null
	}

	if (isEmpty(utenlandskAdresse)) {
		return <TitleValue title="Utenlandsk adresse" value="Ingen verdier satt" />
	}

	return (
		<>
			<TitleValue title="Gatenavn og husnummer" value={utenlandskAdresse.adressenavnNummer} />
			<TitleValue title="Postnummer og -navn" value={utenlandskAdresse.postboksNummerNavn} />
			<TitleValue title="Postkode" value={utenlandskAdresse.postkode} />
			<TitleValue title="By eller sted" value={utenlandskAdresse.bySted} />
			<TitleValue
				title="Land"
				value={utenlandskAdresse.landkode}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
			<TitleValue title="Bygg-/leilighetsinfo" value={utenlandskAdresse.bygningEtasjeLeilighet} />
			<TitleValue title="Region/distrikt/område" value={utenlandskAdresse.regionDistriktOmraade} />
		</>
	)
}
