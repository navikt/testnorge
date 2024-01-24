import React from 'react'
import { DeltBostedData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { Vegadresse } from '@/components/fagsystem/pdlf/bestilling/partials/Vegadresse'
import { Matrikkeladresse } from '@/components/fagsystem/pdlf/bestilling/partials/Matrikkeladresse'
import { UkjentBosted } from '@/components/fagsystem/pdlf/bestilling/partials/UkjentBosted'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type DeltBostedTypes = {
	deltBosted: DeltBostedData
}

export const DeltBosted = ({ deltBosted }: DeltBostedTypes) => {
	if (!deltBosted) {
		return null
	}

	return (
		<>
			<TitleValue
				title="Adressetype"
				value={showLabel('adressetypeDeltBosted', deltBosted.adressetype)}
			/>
			<Vegadresse vegadresse={deltBosted.vegadresse} />
			<Matrikkeladresse matrikkeladresse={deltBosted.matrikkeladresse} />
			<UkjentBosted ukjentBosted={deltBosted.ukjentBosted} />
			<TitleValue
				title="Startdato for kontrakt"
				value={formatDate(deltBosted.startdatoForKontrakt)}
			/>
			<TitleValue
				title="Sluttdato for kontrakt"
				value={formatDate(deltBosted.sluttdatoForKontrakt)}
			/>
		</>
	)
}
