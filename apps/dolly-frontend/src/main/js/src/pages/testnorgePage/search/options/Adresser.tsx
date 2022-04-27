import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'

const options = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

type AdresserProps = {
	formikBag: FormikProps<{}>
}

export const Adresser = ({ formikBag }: AdresserProps) => {
	const [visAvansert, setVisAvansert, setSkjulAvansert] = useBoolean(false)
	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har norsk bostedsadresse'}
				path={'adresser.bostedsadresse.harNorskAdresse'}
				options={options}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har utenlandsk bostedsadresse'}
				path={'adresser.bostedsadresse.harUtenlandskAdresse'}
				options={options}
			/>
			<FormikSelect
				name={'adresser.bostedsadresse.postnummer'}
				label="Bosted - Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={'adresser.bostedsadresse.kommunenummer'}
				label="Bosted - Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				optionHeight={50}
				size="medium"
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har kontaktadresse'}
				path={'adresser.harKontaktadresse'}
				options={options}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har oppholdsadresse'}
				path={'adresser.harOppholdsadresse'}
				options={options}
			/>

			{visAvansert ? (
				<Button onClick={setSkjulAvansert} kind={'collapse'}>
					SKJUL AVANSERTE VALG
				</Button>
			) : (
				<Button onClick={setVisAvansert} kind={'expand'}>
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
	'adresser.harNorskAdresse': 'string',
	'adresser.harUtenlandskAdresse': 'string',
	'adresser.postnummer': 'string',
	'adresser.kommunenummer': 'string',
}
