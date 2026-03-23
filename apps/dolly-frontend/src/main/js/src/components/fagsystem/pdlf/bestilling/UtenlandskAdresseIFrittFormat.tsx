import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { UtenlandskAdresseIFrittFormatData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { AdresseKodeverk } from '@/config/kodeverk'
import { arrayToString } from '@/utils/DataFormatter'

type UtenlandskAdresseTypes = {
	utenlandskAdresseIFrittFormat?: UtenlandskAdresseIFrittFormatData
}

export const UtenlandskAdresseIFrittFormat = ({
	utenlandskAdresseIFrittFormat,
}: UtenlandskAdresseTypes) => {
	if (!utenlandskAdresseIFrittFormat) {
		return null
	}

	if (isEmpty(utenlandskAdresseIFrittFormat)) {
		return <TitleValue title="Utenlandsk adresse i fritt format" value="Ingen verdier satt" />
	}

	return (
		<>
			<TitleValue
				title="Adresselinjer"
				value={arrayToString(utenlandskAdresseIFrittFormat.adresselinjer, ', ')}
			/>

			<TitleValue title="Postkode" value={utenlandskAdresseIFrittFormat.postkode} />
			<TitleValue title="By eller sted" value={utenlandskAdresseIFrittFormat.byEllerStedsnavn} />
			<TitleValue
				title="Land"
				value={utenlandskAdresseIFrittFormat.landkode}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
		</>
	)
}
