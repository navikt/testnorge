import React from 'react'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'

interface IdentifikasjonProps {
	formikBag: FormikProps<{}>
	path: string
}

const options = {
	identtype: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' },
	],
	adressebeskyttelse: [
		{ value: 'FORTROLIG', label: 'Fortrolig' },
		{ value: 'STRENGT_FORTROLIG', label: 'Strengt fortrolig' },
	],
	kjoenn: [
		{ value: 'KVINNE', label: 'Kvinne' },
		{ value: 'MANN', label: 'Mann' },
	],
}

export const Identifikasjon: React.FC<IdentifikasjonProps> = ({
	formikBag,
	path,
}: IdentifikasjonProps) => {
	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Identifikatortype'}
				path={`${path}.identtype`}
				legend={'Velg identifikatortype'}
				options={options.identtype}
			/>

			<RadioGroupOptions
				formikBag={formikBag}
				name={'Adressebeskyttelse'}
				path={`${path}.adressebeskyttelse`}
				legend={'Velg gradering'}
				options={options.adressebeskyttelse}
			/>
			<h4 className="subtittel">Identitet</h4>
			<FormikCheckbox
				name="personinformasjon.identifikasjon.falskIdentitet"
				label="Har falsk identitet"
				size="medium"
			/>
			<FormikCheckbox
				name="personinformasjon.identifikasjon.utenlandskIdentitet"
				label="Har utenlandsk identitet"
				size="medium"
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Kjønn'}
				path={`${path}.kjoenn`}
				legend={'Velg kjønn'}
				options={options.kjoenn}
			/>
		</section>
	)
}

export const IdentifikasjonPaths = {
	'personinformasjon.identifikasjon.falskIdentitet': 'boolean',
	'personinformasjon.identifikasjon.utenlandskIdentitet': 'boolean',
	'personinformasjon.identifikasjon.identtype': 'string',
	'personinformasjon.identifikasjon.adressebeskyttelse': 'string',
	'personinformasjon.identifikasjon.kjoenn': 'string',
}
