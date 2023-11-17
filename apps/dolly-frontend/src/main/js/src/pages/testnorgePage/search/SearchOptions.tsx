import * as _ from 'lodash-es'
import { Alder, AlderPaths } from './partials/Alder'
import { Identifikasjon, IdentifikasjonPaths } from './partials/Identifikasjon'
import { Identer, IdenterPaths } from '@/pages/testnorgePage/search/partials/Identer'
import { Adresser, AdresserPaths } from '@/pages/testnorgePage/search/partials/Adresser'
import { Nasjonalitet, NasjonalitetPaths } from '@/pages/testnorgePage/search/partials/Nasjonalitet'
import { Relasjoner, RelasjonerPaths } from '@/pages/testnorgePage/search/partials/Relasjoner'
import { Personstatus, PersonstatusPaths } from '@/pages/testnorgePage/search/partials/Personstatus'
import { OptionsPanel } from './optionsPanel/OptionsPanel'
import React from 'react'
import { UseFormReturn } from 'react-hook-form/dist/types'

export type SearchOptionsProps = {
	formMethods: UseFormReturn
}

export const getCount = (paths: string[], formMethods: UseFormReturn) => {
	let count = 0
	for (const path of paths) {
		const value = _.get(formMethods.getValues(), path)
		const valueType = typeof value
		if (valueType === 'string') {
			if (value && value !== '') {
				count++
			}
		} else if (valueType === 'boolean') {
			if (value) {
				count++
			}
		} else if (value instanceof Date) {
			count++
		} else if (Array.isArray(value)) {
			count += [...value].filter((n) => n).length
		}
	}
	return count
}

export const SearchOptions: React.FC<SearchOptionsProps> = (props: SearchOptionsProps) => {
	const disabled = getCount(IdenterPaths, props.formMethods) > 0

	return (
		<>
			<h2>Søkealternativer</h2>
			<OptionsPanel
				heading={'Fødsels- eller D-nummer'}
				startOpen
				numSelected={getCount(IdenterPaths, props.formMethods)}
			>
				<Identer formikBag={props.formMethods} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Identifikasjon'}
				numSelected={getCount(IdentifikasjonPaths, props.formMethods)}
				disabled={disabled}
			>
				<Identifikasjon formikBag={props.formMethods} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Personstatus'}
				numSelected={getCount(PersonstatusPaths, props.formMethods)}
				disabled={disabled}
			>
				<Personstatus />
			</OptionsPanel>
			<OptionsPanel
				heading={'Alder'}
				numSelected={getCount(AlderPaths, props.formMethods)}
				disabled={disabled}
			>
				<Alder formMethods={props.formMethods} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Adresser'}
				numSelected={getCount(AdresserPaths, props.formMethods)}
				disabled={disabled}
			>
				<Adresser formMethods={props.formMethods} />
			</OptionsPanel>
			<OptionsPanel
				heading={'Nasjonalitet'}
				numSelected={getCount(NasjonalitetPaths, props.formMethods)}
				disabled={disabled}
			>
				<Nasjonalitet />
			</OptionsPanel>
			<OptionsPanel
				heading={'Relasjoner'}
				numSelected={getCount(RelasjonerPaths, props.formMethods)}
				disabled={disabled}
			>
				<Relasjoner formMethods={props.formMethods} />
			</OptionsPanel>
		</>
	)
}
