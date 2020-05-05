import React from 'react'
import _get from 'lodash/get'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'

export const InntektstubOrgnummerSelect = ({ path, formikBag, locked }) => {
	const setOrgnummer = org => {
		formikBag.setFieldValue(`${path}.virksomhet`, org.value)
		formikBag.setFieldValue(`${path}.opplysningspliktig`, org.juridiskEnhet)
	}

	const getFeilmelding = () => {
		if (locked) return null

		const error = _get(formikBag.errors, `${path}.virksomhet`)
		const tom = _get(formikBag.values, `${path}.virksomhet`) === ''
		const feilmelding = error ? error : tom ? 'Feltet er påkrevd' : null
		return feilmelding ? { feilmelding: feilmelding } : null
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
					disabled={locked}
				/>
			)}
		/>
	)
}
