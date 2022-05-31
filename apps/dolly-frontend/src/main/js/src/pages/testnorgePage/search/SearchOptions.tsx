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

export const getCount = (values: Record<string, string>, formikBag: FormikProps<{}>) => {
	let count = 0
	for (let key in values) {
		const value = _get(formikBag.values, key)
		const valueType = values[key]
		if (valueType === 'string' && value && value !== '') {
			count++
		} else if (valueType === 'list' && value) {
			count += [...value].filter((n) => n).length
		} else if (value) {
			count++
		}
	}
	return count
}

const getNumSelected = (formikBag: FormikProps<{}>) => {
	let count = getCount(IdenterPaths, formikBag)
	if (count > 0) {
		return count
	}
	count += getCount(IdentifikasjonPaths, formikBag)
	count += getCount(PersonstatusPaths, formikBag)
	count += getCount(AlderPaths, formikBag)
	count += getCount(AdresserPaths, formikBag)
	count += getCount(NasjonalitetPaths, formikBag)
	count += getCount(RelasjonerPaths, formikBag)
	return count
}

export const SearchOptions: React.FC<SearchOptionsProps> = (props: SearchOptionsProps) => {
	const disabled = getCount(IdenterPaths, props.formikBag) > 0
	const numSelected = getNumSelected(props.formikBag)

	return (
		<>
			<div className="flexbox--align-center">
				<h2>Personinformasjon</h2>
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
