import React from 'react'
import _get from 'lodash/get'
import _set from 'lodash/set'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'

export const InntektstubOrgnummerSelect = ({ path, formikBag }) => {
	const setOrgnummer = org => {
		formikBag.setFieldValue(`${path}.virksomhet`, org.value)
		formikBag.setFieldValue(`${path}.opplysningspliktig`, org.juridiskEnhet)
	}

	const value = _get(formikBag.values, `${path}.virksomhet`)
	const getFeilmelding = () => {
		const error = _get(formikBag.errors, `${path}.virksomhet`)
		return error
			? { feilmelding: error }
			: value === ''
			? { feilmelding: 'Feltet er påkrevd' }
			: null
	}
	return (
		<OrganisasjonLoader
			filter={response => response.kanHaArbeidsforhold}
			render={data => (
				<DollySelect
					name={`${path}.virksomhet`}
					label="Virksomhet (orgnr/id)"
					options={data}
					size="xlarge"
					onChange={setOrgnummer}
					value={_get(formikBag.values, `${path}.virksomhet`)}
					feil={getFeilmelding()}
					isClearable={false}
				/>
			)}
		/>
	)
}
