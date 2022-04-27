import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'

const options = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

type AdresserProps = {
	formikBag: FormikProps<{}>
	numSelected: number
}

export const Adresser = ({ formikBag, numSelected }: AdresserProps) => {
	const [selectedAdressetyper, setSelectedAdressetyper] = useState(['bostedsadresse'])

	const handleSelectedChange = (type: string) => {
		let newValues = [...selectedAdressetyper]
		if (newValues.includes(type)) {
			newValues = newValues.filter((value) => value !== type)
		} else {
			newValues.push(type)
		}
		setSelectedAdressetyper(newValues)
		formikBag.setFieldValue('adresser.adressetyper', newValues)
	}

	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har norsk adresse'}
				path={'adresser.harNorskAdresse'}
				options={options}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har utenlandsk adresse'}
				path={'adresser.harUtenlandskAdresse'}
				options={options}
			/>
			<FormikSelect
				name={'adresser.postnummer'}
				label="Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				disabled={_get(formikBag.values, 'adresser.harNorskAdresse') === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={'adresser.kommunenummer'}
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				disabled={_get(formikBag.values, 'adresser.harNorskAdresse') === 'N'}
				optionHeight={50}
				size="medium"
			/>
			{numSelected > 0 && (
				<>
					<div className="options-title">Søk i:</div>
					<DollyCheckbox
						label="Bostedsadresse"
						size="medium"
						checked={selectedAdressetyper.includes('bostedsadresse')}
						onChange={() => handleSelectedChange('bostedsadresse')}
					/>
					<DollyCheckbox
						label="Kontaktadresse"
						size="medium"
						checked={selectedAdressetyper.includes('kontaktadresse')}
						onChange={() => handleSelectedChange('kontaktadresse')}
					/>
					<DollyCheckbox
						label="Oppholdsadresse"
						size="medium"
						checked={selectedAdressetyper.includes('oppholdsadresse')}
						onChange={() => handleSelectedChange('oppholdsadresse')}
					/>
				</>
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
