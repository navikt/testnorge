import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { Alder, AlderPaths } from './partials/Alder'
import { Identifikasjon, IdentifikasjonPaths } from './partials/Identifikasjon'
import { Identer, IdenterPaths } from '~/pages/testnorgePage/search/partials/Identer'
import { Adresser, AdresserPaths } from '~/pages/testnorgePage/search/partials/Adresser'
import { Nasjonalitet, NasjonalitetPaths } from '~/pages/testnorgePage/search/partials/Nasjonalitet'
import { Relasjoner, RelasjonerPaths } from '~/pages/testnorgePage/search/partials/Relasjoner'
import { Personstatus, PersonstatusPaths } from '~/pages/testnorgePage/search/partials/Personstatus'
import { OptionsPanel } from './optionsPanel/OptionsPanel'
import styled from 'styled-components'
import Icon from '~/components/ui/icon/Icon'

export type SearchOptionsProps = {
	formikBag: FormikProps<{}>
}

const IconContainer = styled.div`
	margin: 0 0 0 auto;
	cursor: pointer;
`

export const getCount = (paths: string[], formikBag: FormikProps<{}>) => {
	let count = 0
	for (let path of paths) {
		const value = _get(formikBag.values, path)
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

const getNumSelected = (formikBag: FormikProps<{}>) => {
	let count = getCount(IdenterPaths, formikBag)
	if (count > 0) {
		return count
	}
	const allPaths = [
		...IdentifikasjonPaths,
		...PersonstatusPaths,
		...AlderPaths,
		...AdresserPaths,
		...NasjonalitetPaths,
		...RelasjonerPaths,
	]
	return getCount(allPaths, formikBag)
}

export const SearchOptions: React.FC<SearchOptionsProps> = (props: SearchOptionsProps) => {
	const disabled = getCount(IdenterPaths, props.formikBag) > 0
	const numSelected = getNumSelected(props.formikBag)

	return (
		<>
			<div className="flexbox--align-center">
				<h2>Søkealternativer</h2>
				{numSelected > 0 && (
					<IconContainer
						onClick={() => {
							props.formikBag.resetForm()
						}}
					>
						<Icon size={20} kind={'trashcan'} />
					</IconContainer>
				)}
			</div>
			<OptionsPanel
				heading={'Fødsels- eller D-nummer'}
				startOpen
				numSelected={getCount(IdenterPaths, props.formikBag)}
			>
				<Identer formikBag={props.formikBag} />
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
