import React, { useState } from 'react'
import { FormikProps } from 'formik'
import { OptionsSection } from './options/OptionsSection'
import {
	IdentNummer,
	IdentPaths,
	Alder,
	AlderPaths,
	Statsborgerskap,
	StatsborgerskapPaths,
	Sivilstand,
	SivilstandPaths,
	Barn,
	BarnPaths,
	Identitet,
	IdentitetPaths,
	Diverse,
	DiversePaths,
} from './options/Options'
import _get from 'lodash/get'

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

export const SearchOptions: React.FC<SearchOptionsProps> = (props: SearchOptionsProps) => {
	const [selectionColor, setSelectionColor] = useState('blue')

	return (
		<>
			<h2>Personinformasjon</h2>
			<OptionsSection
				heading={'Fødsels- eller D-nummer'}
				options={<IdentNummer formikBag={props.formikBag} />}
				startOpen={true}
				numSelected={getCount(IdentPaths, props.formikBag)}
			/>
			<OptionsSection
				heading={'Alder'}
				options={<Alder />}
				numSelected={getCount(AlderPaths, props.formikBag)}
				selectionColor={selectionColor}
			/>
			<OptionsSection
				heading={'Statsborgerskap'}
				options={<Statsborgerskap />}
				numSelected={getCount(StatsborgerskapPaths, props.formikBag)}
				selectionColor={selectionColor}
			/>
			<OptionsSection
				heading={'Sivilstand'}
				options={<Sivilstand />}
				numSelected={getCount(SivilstandPaths, props.formikBag)}
				selectionColor={selectionColor}
			/>
			<OptionsSection
				heading={'Barn'}
				options={<Barn />}
				numSelected={getCount(BarnPaths, props.formikBag)}
				selectionColor={selectionColor}
			/>
			<OptionsSection
				heading={'Identitet'}
				options={<Identitet />}
				numSelected={getCount(IdentitetPaths, props.formikBag)}
				selectionColor={selectionColor}
			/>
			<OptionsSection
				heading={'Diverse'}
				options={<Diverse />}
				numSelected={getCount(DiversePaths, props.formikBag)}
				selectionColor={selectionColor}
			/>
		</>
	)
}
