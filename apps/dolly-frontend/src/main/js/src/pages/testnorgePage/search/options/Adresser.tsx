import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

const bostedPath = 'adresser.bostedsadresse'
const kontaktPath = 'adresser.kontaktadresse'
const oppholdPath = 'adresser.oppholdsadresse'

const oppholdAnnetStedOptions = [
	{ value: 'MILITAER', label: 'Militær' },
	{ value: 'UTENRIKS', label: 'Utenriks' },
	{ value: 'PAA_SVALBARD', label: 'På Svalbard' },
	{ value: 'PENDLER', label: 'Pendler' },
]

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
		<FormikCheckbox
			name={`${kontaktPath}.kontaktadresseForDoedsbo`}
			label="Har kontaktadresse for dødsbo"
			size="medium"
		/>
		<h4 className="subtittel">Oppholdsadresse</h4>
		<FormikCheckbox name={`${oppholdPath}.norskAdresse`} label="Har norsk adresse" size="medium" />
		<FormikCheckbox
			name={`${oppholdPath}.utenlandskAdresse`}
			label="Har utenlandsk adresse"
			size="medium"
		/>
		<FormikSelect
			name={`${oppholdPath}.oppholdAnnetSted`}
			label="Opphold annet sted"
			options={oppholdAnnetStedOptions}
			size="medium"
		/>
	</section>
)

export const AdresserPaths = {
	[bostedPath + '.postnummer']: 'string',
	[bostedPath + '.kommunenummer']: 'string',
	[kontaktPath + '.norskAdresse']: 'boolean',
	[kontaktPath + '.utenlandskAdresse']: 'boolean',
	[kontaktPath + '.kontaktadresseForDoedsbo']: 'boolean',
	[oppholdPath + '.norskAdresse']: 'boolean',
	[oppholdPath + '.utenlandskAdresse']: 'boolean',
	[oppholdPath + '.oppholdAnnetSted']: 'string',
}
