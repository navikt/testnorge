import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Postboksadresse = () => {
	const norskAdresse = 'tpsf.midlertidigAdresse.norskAdresse'

	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${norskAdresse}.postboksnr`} label="Postboksnummer" type="number" />
			<FormikTextInput
				name={`${norskAdresse}.postboksAnlegg`}
				label="Postboksanlegg"
				size="large"
			/>
			<FormikSelect
				name={`${norskAdresse}.postnr`}
				label="Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				size="large"
				isClearable={false}
			/>
		</div>
	)
}
