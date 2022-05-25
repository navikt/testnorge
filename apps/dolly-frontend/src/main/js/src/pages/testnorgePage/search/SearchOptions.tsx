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

export const SearchOptions: React.FC<SearchOptionsProps> = (props: SearchOptionsProps) => {
	const disabled = getCount(IdentSearchPaths, props.formikBag) > 0
	return (
		<>
			<h2>Personinformasjon</h2>
			<OptionsPanel
				heading={'Fødsels- eller D-nummer'}
				startOpen
				numSelected={getCount(IdentSearchPaths, props.formikBag)}
			>
				<IdentSearch formikBag={props.formikBag} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Identifikasjon'}
				numSelected={getCount(IdentifikasjonPaths, props.formikBag)}
				disabled={disabled}
			>
				<Identifikasjon formikBag={props.formikBag} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Personstatus'}
				numSelected={getCount(PersonstatusPaths, props.formikBag)}
				disabled={disabled}
			>
				<Personstatus />
			</OptionsPanel>
			<OptionsPanel
				heading={'Alder'}
				numSelected={getCount(AlderPaths, props.formikBag)}
				disabled={disabled}
			>
				<Alder />
			</OptionsPanel>
			<OptionsPanel
				heading={'Adresser'}
				numSelected={getCount(AdresserPaths, props.formikBag)}
				disabled={disabled}
			>
				<Adresser formikBag={props.formikBag} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Nasjonalitet'}
				numSelected={getCount(NasjonalitetPaths, props.formikBag)}
				disabled={disabled}
			>
				<Nasjonalitet />
			</OptionsPanel>
			<OptionsPanel
				heading={'Relasjoner'}
				numSelected={getCount(RelasjonerPaths, props.formikBag)}
				disabled={disabled}
			>
				<Relasjoner formikBag={props.formikBag} />
			</OptionsPanel>
		</>
	)
}
