import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Stedsadresse = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput
				name="tpsf.midlertidigAdresse.norskAdresse.eiendomsnavn"
				label="Eiendomsnavn"
				size="large"
			/>
			<FormikSelect
				name="tpsf.midlertidigAdresse.norskAdresse.postnr"
				label="Postnummer"
				kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
				size="large"
				isClearable={false}
			/>
		</div>
	)
}
