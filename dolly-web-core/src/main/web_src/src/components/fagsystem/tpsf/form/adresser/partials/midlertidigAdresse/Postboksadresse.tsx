import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Postboksadresse = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput
				name="tpsf.midlertidigAdresse.norskAdresse.postboksnr"
				label="Postboksnummer"
				type="number"
			/>
			<FormikTextInput
				name="tpsf.midlertidigAdresse.norskAdresse.postboksAnlegg"
				label="Postboksanlegg"
				size="large"
			/>
			<FormikSelect
				name="tpsf.midlertidigAdresse.norskAdresse.postnr"
				label="Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				size="large"
				isClearable={false}
			/>
		</div>
	)
}
