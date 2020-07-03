import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const UtenlandskAdresse = () => {
	const utenlandskAdresse = 'tpsf.midlertidigAdresse.utenlandskAdresse'

	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${utenlandskAdresse}.postLinje1`} label="Postlinje 1" size="large" />
			<FormikTextInput name={`${utenlandskAdresse}.postLinje2`} label="Postlinje 2" size="large" />
			<FormikTextInput name={`${utenlandskAdresse}.postLinje3`} label="Postlinje 3" size="large" />
			<FormikSelect
				name={`${utenlandskAdresse}.postLand`}
				label="Land"
				kodeverk={AdresseKodeverk.PostadresseLand}
				size="large"
			/>
		</div>
	)
}
