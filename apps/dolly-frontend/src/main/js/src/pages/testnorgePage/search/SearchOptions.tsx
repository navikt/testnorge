import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { OptionsSection } from './options/OptionsSection'
import { Alder, AlderPaths } from './options/Alder'
import { Identifikasjon, IdentifikasjonPaths } from './options/Identifikasjon'
import { IdentSearch, IdentSearchPaths } from '~/pages/testnorgePage/search/options/IdentSearch'
import { Adresser, AdresserPaths } from '~/pages/testnorgePage/search/options/Adresser'
import { Nasjonalitet, NasjonalitetPaths } from '~/pages/testnorgePage/search/options/Nasjonalitet'
import { Relasjoner, RelasjonerPaths } from '~/pages/testnorgePage/search/options/Relasjoner'
import { Personstatus, PersonstatusPaths } from '~/pages/testnorgePage/search/options/Personstatus'

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
				heading={'Personstatus'}
				options={<Personstatus />}
				numSelected={getCount(PersonstatusPaths, props.formikBag)}
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
				options={<Adresser formikBag={props.formikBag} />}
				numSelected={getCount(AdresserPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Nasjonalitet'}
				options={<Nasjonalitet />}
				numSelected={getCount(NasjonalitetPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
			<OptionsSection
				heading={'Relasjoner'}
				options={<Relasjoner formikBag={props.formikBag} />}
				numSelected={getCount(RelasjonerPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			/>
		</>
	)
}
