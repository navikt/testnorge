import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { InntektstubKodeverk } from '~/config/kodeverk'

interface ArbeidsgiverVisning {
	data: Arbeidsgiver
}

type Arbeidsgiver = {
	orgnummer?: string
	virksomhetsnummer: string
	kontaktinformasjon?: any
}

export default ({ data }: ArbeidsgiverVisning) => {
	if (!data) return null

	return (
		<React.Fragment>
			<h4>Arbeidsgiver</h4>
			<TitleValue title="Opplysningspliktig virksomhet" value={data.virksomhetsnummer} />
			<TitleValue title="Virksomhet" value={data.orgnummer} />
		</React.Fragment>
	)
}
