import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'

const booleanPptions = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

const options = {
	boolean: [
		{ value: 'Y', label: 'Ja' },
		{ value: 'N', label: 'Nei' },
	],
	borINorge: [
		{ value: 'Y', label: 'Har norsk adresse' },
		{ value: 'N', label: 'Har utenlandsk adresse' },
	],
}

type AdresserProps = {
	formikBag: FormikProps<{}>
}

const bostedPath = 'adresser.bostedsadresse'
const kontaktPath = 'adresser.harKontaktadresse'
const oppholdPath = 'adresser.harOppholdsadresse'

export const Adresser = ({ formikBag }: AdresserProps) => {
	const [visAvansert, setVisAvansert, setSkjulAvansert] = useBoolean(false)
	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Bostedsadresse'}
				path={`${bostedPath}.borINorge`}
				options={options.borINorge}
			/>
			<FormikSelect
				name={`${bostedPath}.postnummer`}
				label="Bosted - Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={`${bostedPath}.kommunenummer`}
				label="Bosted - Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				optionHeight={50}
				size="medium"
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har kontaktadresse'}
				path={kontaktPath}
				options={options.boolean}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har oppholdsadresse'}
				path={oppholdPath}
				options={options.boolean}
			/>

			{visAvansert ? (
				<Button onClick={setSkjulAvansert} kind={'collapse'}>
					SKJUL AVANSERTE VALG
				</Button>
			) : (
				<Button onClick={setVisAvansert} kind={'expand'} style={{ marginBottom: '10px' }}>
					VIS AVANSERTE VALG
				</Button>
			)}
			{visAvansert && (
				<div>
					<div className="options-title">Historikk</div>
					<FormikCheckbox
						name={'adresser.bostedsadresse.harHistorikk'}
						label="Har bostedshistorikk"
						size="small"
					/>
				</div>
			)}
		</section>
	)
}

export const AdresserPaths = {
	[bostedPath + '.harNorskAdresse']: 'string',
	[bostedPath + '.harUtenlandskAdresse']: 'string',
	[bostedPath + '.postnummer']: 'string',
	[bostedPath + '.kommunenummer']: 'string',
	[kontaktPath]: 'string',
	[oppholdPath]: 'string',
}
