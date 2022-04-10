import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

const bostedPath = 'personinformasjon.adresser.bostedsadresse'
const kontaktPath = 'personinformasjon.adresser.kontaktadresse'
const oppholdPath = 'personinformasjon.adresser.oppholdsadresse'

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
		<h4 className="subtittel">Oppholdsadresse</h4>
		<FormikCheckbox name={`${oppholdPath}.norskAdresse`} label="Har norsk adresse" size="medium" />
		<FormikCheckbox
			name={`${oppholdPath}.utenlandskAdresse`}
			label="Har utenlandsk adresse"
			size="medium"
		/>
		<FormikCheckbox
			name={`${oppholdPath}.oppholdAnnetSted`}
			label="Har opphold annet sted"
			size="medium"
		/>
	</section>
)

export const AdresserPaths = {
	[bostedPath + '.postnummer']: 'string',
	[bostedPath + '.kommunenummer']: 'string',
	[kontaktPath + '.norskAdresse']: 'boolean',
	[kontaktPath + '.utenlandskAdresse']: 'boolean',
	[oppholdPath + '.norskAdresse']: 'boolean',
	[oppholdPath + '.utenlandskAdresse']: 'boolean',
	[oppholdPath + '.oppholdAnnetSted']: 'boolean',
}
