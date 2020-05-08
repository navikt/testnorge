import React from 'react'
import _get from 'lodash/get'
import _set from 'lodash/set'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'

export const InntektstubOrgnummerSelect = ({ path, formikBag, locked, versjonInfo }) => {
	const setOrgnummer = org => {
		const inntektinfo = { ...formikBag.values }
		_set(inntektinfo, `${path}.virksomhet`, org.value)
		_set(inntektinfo, `${path}.opplysningspliktig`, org.juridiskEnhet)

		if (versjonInfo.gjeldendeInntektMedHistorikk) {
			versjonInfo.underversjoner.forEach(versjon => {
				_set(inntektinfo, `${versjonInfo.path}[${versjon}].virksomhet`, org.value)
				_set(inntektinfo, `${versjonInfo.path}[${versjon}].opplysningspliktig`, org.juridiskEnhet)
			})
		}
		formikBag.setFieldValue('inntektstub', inntektinfo.inntektstub)
	}

	const value = _get(formikBag.values, `${path}.virksomhet`)
	const getFeilmelding = () => {
		if (locked) return null
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
					disabled={locked}
				/>
			)}
		/>
	)
}
