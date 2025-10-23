import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { UkjentBostedData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { AdresseKodeverk } from '@/config/kodeverk'

type UkjentBostedTypes = {
	ukjentBosted?: UkjentBostedData
}

export const UkjentBosted = ({ ukjentBosted }: UkjentBostedTypes) => {
	if (!ukjentBosted) {
		return null
	}

	if (isEmpty(ukjentBosted)) {
		return <TitleValue title="Ukjent bosted" value="Ingen verdier satt" />
	}

	return (
		<TitleValue
			title="Bostedskommune"
			value={ukjentBosted.bostedskommune}
			kodeverk={AdresseKodeverk.Kommunenummer}
		/>
	)
}
