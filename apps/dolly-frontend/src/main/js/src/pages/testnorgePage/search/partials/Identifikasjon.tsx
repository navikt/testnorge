import { FormikProps } from 'formik'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { RadioGroupOptions } from '@/pages/testnorgePage/search/radioGroupOptions/RadioGroupOptions'
import React from 'react'

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
	falsk: 'identifikasjon.falskIdentitet',
	utenlandsk: 'identifikasjon.utenlandskIdentitet',
	historikk: 'identifikasjon.identHistorikk',
	kjoenn: 'kjoenn',
}

export const Identifikasjon: React.FC<IdentifikasjonProps> = ({
	formikBag,
}: IdentifikasjonProps) => {
	return (
		<section>
			<RadioGroupOptions
				formMethods={formMethods}
				name={'Identifikatortype'}
				path={paths.identtype}
				legend={'Velg identifikatortype'}
				options={options.identtype}
			/>
			<div className="options-title">Identitet</div>
			<FormikCheckbox name={paths.falsk} label="Har falsk identitet" />
			<FormikCheckbox name={paths.utenlandsk} label="Har utenlandsk identitet" />
			<FormikCheckbox name={paths.historikk} label="Har identhistorikk" />
			<RadioGroupOptions
				formMethods={formMethods}
				name={paths.kjoenn}
				path={paths.kjoenn}
				legend={'Velg kjønn'}
				options={options.kjoenn}
			/>
		</section>
	)
}

export const IdentifikasjonPaths = Object.values(paths)
