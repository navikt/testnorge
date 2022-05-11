import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { Alder, AlderPaths } from './partials/Alder'
import { Identifikasjon, IdentifikasjonPaths } from './partials/Identifikasjon'
import { IdentSearch, IdentSearchPaths } from '~/pages/testnorgePage/search/partials/IdentSearch'
import { Adresser, AdresserPaths } from '~/pages/testnorgePage/search/partials/Adresser'
import { Nasjonalitet, NasjonalitetPaths } from '~/pages/testnorgePage/search/partials/Nasjonalitet'
import { Relasjoner, RelasjonerPaths } from '~/pages/testnorgePage/search/partials/Relasjoner'
import { Personstatus, PersonstatusPaths } from '~/pages/testnorgePage/search/partials/Personstatus'
import { OptionsPanel } from './optionsPanel/OptionsPanel'

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
			<OptionsPanel
				heading={'Fødsels- eller D-nummer'}
				startOpen={true}
				numSelected={getCount(IdentSearchPaths, props.formikBag)}
			>
				<IdentSearch formikBag={props.formikBag} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Identifikasjon'}
				numSelected={getCount(IdentifikasjonPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			>
				<Identifikasjon formikBag={props.formikBag} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Personstatus'}
				numSelected={getCount(PersonstatusPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			>
				<Personstatus />
			</OptionsPanel>
			<OptionsPanel
				heading={'Alder'}
				numSelected={getCount(AlderPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			>
				<Alder />
			</OptionsPanel>
			<OptionsPanel
				heading={'Adresser'}
				numSelected={getCount(AdresserPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			>
				<Adresser formikBag={props.formikBag} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Nasjonalitet'}
				numSelected={getCount(NasjonalitetPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			>
				<Nasjonalitet />
			</OptionsPanel>
			<OptionsPanel
				heading={'Relasjoner'}
				numSelected={getCount(RelasjonerPaths, props.formikBag)}
				selectionColor={getSelectionColor(props.formikBag)}
			>
				<Relasjoner formikBag={props.formikBag} />
			</OptionsPanel>
		</>
	)
}
