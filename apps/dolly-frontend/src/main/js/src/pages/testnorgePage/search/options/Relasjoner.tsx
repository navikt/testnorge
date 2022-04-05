import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'

const options = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

type RelasjonerProps = {
	formikBag: FormikProps<{}>
	path: string
}

export const Relasjoner = ({ formikBag, path }: RelasjonerProps) => (
	<section>
		<FormikSelect
			name={`${path}.sivilstand`}
			label="Sivilstand"
			options={[
				{ value: 'GIFT', label: 'Gift' },
				{ value: 'UGIFT', label: 'Ugift' },
				{ value: 'SEPARERT', label: 'Separert' },
				{ value: 'SKILT', label: 'Skilt' },
			]}
			size="medium"
		/>
		<RadioGroupOptions
			formikBag={formikBag}
			name={'Har barn'}
			path={`${path}.harBarn`}
			options={options}
		/>
		<RadioGroupOptions
			formikBag={formikBag}
			name={'Har dødfødt barn'}
			path={`${path}.harDoedfoedtBarn`}
			options={options}
		/>
	</section>
)

export const RelasjonerPaths = {
	'personinformasjon.relasjoner.sivilstand': 'string',
	'personinformasjon.relasjoner.harBarn': 'string',
	'personinformasjon.relasjoner.harDoedfoedtBarn': 'string',
}
