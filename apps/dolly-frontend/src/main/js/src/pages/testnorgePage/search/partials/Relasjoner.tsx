import React, { useEffect, useState } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/radioGroupOptions/RadioGroupOptions'
import { FormikProps } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _get from 'lodash/get'
import { yesNoOptions } from '~/pages/testnorgePage/utils'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

const paths = {
	sivistand: 'sivilstand.type',
	harBarn: 'relasjoner.harBarn',
	harDoedfoedtBarn: 'relasjoner.harDoedfoedtBarn',
	forelderBarnRelasjoner: 'relasjoner.forelderBarnRelasjoner',
	foreldreansvar: 'relasjoner.foreldreansvar',
}

type RelasjonerProps = {
	formikBag: FormikProps<{}>
}

const foreldreRoller = {
	FAR: 'FAR',
	MOR: 'MOR',
	MEDMOR: 'MEDMOR',
}

const barnOptions = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
	{ value: 'M', label: 'Har flere barn' },
]

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
					{ value: 'REGISTRERT_PARTNER', label: 'Registrert partner' },
					{ value: 'SEPARERT_PARTNER', label: 'Separert partner' },
				]}
				size="medium"
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har barn'}
				path={paths.harBarn}
				options={barnOptions}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har dødfødt barn'}
				path={paths.harDoedfoedtBarn}
				options={yesNoOptions}
			/>
			<div className="options-title">Har forelder</div>
			<DollyCheckbox
				label={'Far'}
				checked={_get(formikBag.values, paths.forelderBarnRelasjoner).includes(foreldreRoller.FAR)}
				onChange={() => handleForelderChange(foreldreRoller.FAR)}
				size="small"
			/>
			<DollyCheckbox
				label={'Mor'}
				checked={_get(formikBag.values, paths.forelderBarnRelasjoner).includes(foreldreRoller.MOR)}
				onChange={() => handleForelderChange(foreldreRoller.MOR)}
				size="small"
			/>
			<DollyCheckbox
				label={'Medmor'}
				checked={_get(formikBag.values, paths.forelderBarnRelasjoner).includes(
					foreldreRoller.MEDMOR
				)}
				onChange={() => handleForelderChange(foreldreRoller.MEDMOR)}
				size="small"
			/>
			<FormikSelect
				name={paths.foreldreansvar}
				label="Hvem har foreldreansvar"
				options={Options('foreldreansvar')}
				size="medium"
				info="Velg hvem som har foreldreansvaret for personen du søker etter."
			/>
		</section>
	)
}

export const RelasjonerPaths = Object.values(paths)
