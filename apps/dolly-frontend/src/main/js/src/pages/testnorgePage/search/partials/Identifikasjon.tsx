import React from 'react'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/radioGroupOptions/RadioGroupOptions'
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

const paths = {
	identtype: 'identifikasjon.identtype',
	adressebeskyttelse: 'identifikasjon.adressebeskyttelse',
	falsk: 'identifikasjon.falskIdentitet',
	utenlandsk: 'identifikasjon.utenlandskIdentitet',
	kjoenn: 'kjoenn',
}

export const Identifikasjon: React.FC<IdentifikasjonProps> = ({
	formikBag,
}: IdentifikasjonProps) => {
	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Identifikatortype'}
				path={paths.identtype}
				legend={'Velg identifikatortype'}
				options={options.identtype}
			/>
			<FormikSelect
				name={paths.adressebeskyttelse}
				label="Adressebeskyttelse"
				options={options.adressebeskyttelse}
				size="medium"
			/>
			<div className="options-title">Identitet</div>
			<FormikCheckbox name={paths.falsk} label="Har falsk identitet" size="medium" />
			<FormikCheckbox name={paths.utenlandsk} label="Har utenlandsk identitet" size="medium" />
			<RadioGroupOptions
				formikBag={formikBag}
				name={paths.kjoenn}
				path={paths.kjoenn}
				legend={'Velg kjønn'}
				options={options.kjoenn}
			/>
		</section>
	)
}

export const IdentifikasjonPaths = Object.values(paths)
