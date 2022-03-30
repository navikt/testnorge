import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { OptionsSection } from './options/OptionsSection'
import {
	Alder,
	AlderPaths,
	Statsborgerskap,
	StatsborgerskapPaths,
	Sivilstand,
	SivilstandPaths,
	Barn,
	BarnPaths,
	Diverse,
	DiversePaths,
} from './options/Options'
import { Identifikasjon, IdentifikasjonPaths } from './options/Identifikasjon'
import { IdentSearch, IdentSearchPaths } from '~/pages/testnorgePage/search/options/IdentSearch'
import { Adresser, AdresserPaths } from '~/pages/testnorgePage/search/options/Adresser'

export type SearchOptionsProps = {
	formikBag: FormikProps<{}>
}

export const getCount = (values: Record<string, string>, formikBag: FormikProps<{}>) => {
	let count = 0
	for (let key in values) {
		const value = _get(formikBag.values, key)
		const valueType = values[key]
		if (valueType === 'string' && value && value !== '') {
			count++
		} else if (valueType === 'list' && value) {
			count += value.length
		} else if (valueType === 'ident' && value && value.match(/^\d{11}$/) != null) {
			count++
		} else if (value) {
			count++
		}
	}
	return count
}

const getSelectionColor = (formikBag: FormikProps<{}>) => {
	return getCount(IdentSearchPaths, formikBag) > 0 ? 'grey' : 'blue'
}

export const SearchOptions: React.FC<SearchOptionsProps> = (props: SearchOptionsProps) => {
	return (
		<>
			<h2>Personinformasjon</h2>
			<OptionsSection
				heading={'Fødsels- eller D-nummer'}
				options={<IdentSearch formikBag={props.formikBag} />}
				startOpen={true}
				numSelected={getCount(IdentSearchPaths, props.formikBag)}
			/>
			<OptionsSection
				heading={'Identifikasjon'}
				options={<Identifikasjon formikBag={props.formikBag} />}
				numSelected={getCount(IdentifikasjonPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Alder'}
				options={<Alder />}
				numSelected={getCount(AlderPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Adresser'}
				options={<Adresser />}
				numSelected={getCount(AdresserPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Statsborgerskap'}
				options={<Statsborgerskap />}
				numSelected={getCount(StatsborgerskapPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Sivilstand'}
				options={<Sivilstand />}
				numSelected={getCount(SivilstandPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Barn'}
				options={<Barn />}
				numSelected={getCount(BarnPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Diverse'}
				options={<Diverse />}
				numSelected={getCount(DiversePaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
		</>
	)
}
