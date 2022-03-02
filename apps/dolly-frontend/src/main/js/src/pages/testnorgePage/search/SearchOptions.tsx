import React from 'react'
import { FormikProps } from 'formik'
import { OptionsSection } from './options/OptionsSection'
import {
	IdentNummer,
	Alder,
	Statsborgerskap,
	Sivilstand,
	Barn,
	Identitet,
	Diverse,
} from './options/Options'

type SearchOptionsProps = {
	formikBag: FormikProps<{}>
}

export const SearchOptions = (props: SearchOptionsProps) => (
	<>
		<h2>Personinformasjon</h2>
		<OptionsSection
			heading={'Fødsels- eller D-nummer'}
			options={<IdentNummer formikBag={props.formikBag} />}
			startOpen={true}
			formikBag={props.formikBag}
		/>
		<OptionsSection heading={'Alder'} options={<Alder />} formikBag={props.formikBag} />
		<OptionsSection
			heading={'Statsborgerskap'}
			options={<Statsborgerskap />}
			formikBag={props.formikBag}
		/>
		<OptionsSection heading={'Sivilstand'} options={<Sivilstand />} formikBag={props.formikBag} />
		<OptionsSection heading={'Barn'} options={<Barn />} formikBag={props.formikBag} />
		<OptionsSection heading={'Identitet'} options={<Identitet />} formikBag={props.formikBag} />
		<OptionsSection heading={'Diverse'} options={<Diverse />} formikBag={props.formikBag} />
	</>
)
