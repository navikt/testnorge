import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'

const bostedPath = 'personinformasjon.adresser.bostedsadresse'
const kontaktPath = 'personinformasjon.adresser.kontaktadresse'

export const Adresser = () => (
	<section>
		<h4 className="subtittel">Boadresse</h4>
		<FormikSelect
			name={`${bostedPath}.postnummer`}
			label="Postnummer"
			kodeverk={AdresseKodeverk.Postnummer}
			optionHeight={50}
			size="medium"
		/>
		<FormikSelect
			name={`${bostedPath}.kommunenummer`}
			label="Kommunenummer"
			kodeverk={AdresseKodeverk.Kommunenummer}
			optionHeight={50}
			size="medium"
		/>
		<h4 className="subtittel">Kontaktadresse</h4>
		<FormikCheckbox name={`${kontaktPath}.norskAdresse`} label="Har norsk adresse" size="medium" />
		<FormikCheckbox
			name={`${kontaktPath}.utenlandskAdresse`}
			label="Har utenlandsk adresse"
			size="medium"
		/>
	</section>
)

export const AdresserPaths = {
	[bostedPath + '.postnummer']: 'string',
	[bostedPath + '.kommunenummer']: 'string',
	[kontaktPath + '.norskAdresse']: 'boolean',
	[kontaktPath + '.utenlandskAdresse']: 'boolean',
}
