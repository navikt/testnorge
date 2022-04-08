import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'

const options = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

const paths = {
	sivistand: 'personinformasjon.relasjoner.sivilstand',
	harBarn: 'personinformasjon.relasjoner.harBarn',
	harDoedfoedtBarn: 'personinformasjon.relasjoner.harDoedfoedtBarn',
}

type RelasjonerProps = {
	formikBag: FormikProps<{}>
}

export const Relasjoner = ({ formikBag }: RelasjonerProps) => (
	<section>
		<FormikSelect
			name={paths.sivistand}
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
			path={paths.harBarn}
			options={options}
		/>
		<RadioGroupOptions
			formikBag={formikBag}
			name={'Har dødfødt barn'}
			path={paths.harDoedfoedtBarn}
			options={options}
		/>
	</section>
)

export const RelasjonerPaths = {
	[paths.sivistand]: 'string',
	[paths.harBarn]: 'string',
	[paths.harDoedfoedtBarn]: 'string',
}
