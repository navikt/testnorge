import React, { useEffect, useState } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _get from 'lodash/get'

const options = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

const paths = {
	sivistand: 'personinformasjon.relasjoner.sivilstand',
	harBarn: 'personinformasjon.relasjoner.harBarn',
	harDoedfoedtBarn: 'personinformasjon.relasjoner.harDoedfoedtBarn',
	forelderBarnRelasjoner: 'personinformasjon.relasjoner.forelderBarnRelasjoner',
}

type RelasjonerProps = {
	formikBag: FormikProps<{}>
}

const foreldreRoller = {
	FAR: 'FAR',
	MOR: 'MOR',
	MEDMOR: 'MEDMOR',
}

export const Relasjoner = ({ formikBag }: RelasjonerProps) => {
	const [foreldre, setForeldre] = useState(_get(formikBag.values, paths.forelderBarnRelasjoner))
	const handleForelderChange = (relasjon: string) => {
		let nyeForeldre = [...foreldre]
		if (foreldre.includes(relasjon)) {
			nyeForeldre = nyeForeldre.filter((item: string) => item !== relasjon)
		} else {
			nyeForeldre.push(relasjon)
		}
		setForeldre(nyeForeldre)
	}

	useEffect(() => {
		formikBag.setFieldValue(paths.forelderBarnRelasjoner, foreldre)
	}, [foreldre])

	return (
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
			<div className="options-title">Har forelder</div>
			<DollyCheckbox
				label={foreldreRoller.FAR}
				onChange={() => handleForelderChange(foreldreRoller.FAR)}
				checkboxMargin={false}
				size="small"
			/>
			<DollyCheckbox
				label={foreldreRoller.MOR}
				onChange={() => handleForelderChange(foreldreRoller.MOR)}
				checkboxMargin={false}
				size="small"
			/>
			<DollyCheckbox
				label={foreldreRoller.MEDMOR}
				onChange={() => handleForelderChange(foreldreRoller.MEDMOR)}
				checkboxMargin={false}
				size="small"
			/>
		</section>
	)
}

export const RelasjonerPaths = {
	[paths.sivistand]: 'string',
	[paths.harBarn]: 'string',
	[paths.harDoedfoedtBarn]: 'string',
	[paths.forelderBarnRelasjoner]: 'list',
}
