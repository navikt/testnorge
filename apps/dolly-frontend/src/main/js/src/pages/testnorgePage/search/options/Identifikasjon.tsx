import React from 'react'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

interface IdentifikasjonProps {
	formikBag: FormikProps<{}>
}

const options = {
	identtype: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' },
	],
	adressebeskyttelse: [
		{ value: 'FORTROLIG', label: 'Fortrolig' },
		{ value: 'STRENGT_FORTROLIG', label: 'Strengt fortrolig' },
		{ value: 'INGEN', label: 'Ingen' },
	],
	kjoenn: [
		{ value: 'KVINNE', label: 'Kvinne' },
		{ value: 'MANN', label: 'Mann' },
	],
}

const identifikasjonPath = 'personinformasjon.identifikasjon'
const kjoennPath = 'personinformasjon.kjoenn'

export const Identifikasjon: React.FC<IdentifikasjonProps> = ({
	formikBag,
}: IdentifikasjonProps) => {
	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Identifikatortype'}
				path={`${identifikasjonPath}.identtype`}
				legend={'Velg identifikatortype'}
				options={options.identtype}
			/>
			<FormikSelect
				name={`${identifikasjonPath}.adressebeskyttelse`}
				label="Adressebeskyttelse"
				options={options.adressebeskyttelse}
				size="medium"
			/>
			<div className="options-title">Identitet</div>
			<FormikCheckbox
				name={`${identifikasjonPath}.falskIdentitet`}
				label="Har falsk identitet"
				size="medium"
			/>
			<FormikCheckbox
				name={`${identifikasjonPath}.utenlandskIdentitet`}
				label="Har utenlandsk identitet"
				size="medium"
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={kjoennPath}
				path={kjoennPath}
				legend={'Velg kjønn'}
				options={options.kjoenn}
			/>
		</section>
	)
}

export const IdentifikasjonPaths = {
	[identifikasjonPath + '.falskIdentitet']: 'boolean',
	[identifikasjonPath + '.utenlandskIdentitet']: 'boolean',
	[identifikasjonPath + '.identtype']: 'string',
	[identifikasjonPath + '.adressebeskyttelse']: 'string',
	[kjoennPath]: 'string',
}
