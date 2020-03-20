import React from 'react'
import _get from 'lodash/get'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const OrgnummerForm = ({ path, formikBag, type }) => {
	const organisasjoner = []

	const setOrgnummer = org => {
		formikBag.setFieldValue(`${path}.virksomhet`, org.value)
		formikBag.setFieldValue(`${path}.opplysningspliktig`, org.juridiskEnhet)
	}

	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentOrgnr().then(response => {
					response.liste.forEach(org => {
						org.juridiskEnhet &&
							organisasjoner.push({
								value: org.orgnr,
								label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`,
								juridiskEnhet: org.juridiskEnhet
							})
					})
					return organisasjoner
				})
			}
			render={data =>
				type === 'inntektstub' ? (
					<DollySelect
						name={`${path}.virksomhet`}
						label="Virksomhet (orgnr/id)"
						options={data}
						size="xlarge"
						onChange={setOrgnummer}
						value={_get(formikBag.values, `${path}.virksomhet`)}
						feil={
							_get(formikBag.values, `${path}.virksomhet`) === '' && {
								feilmelding: 'Feltet er påkrevd'
							}
						}
						isClearable={false}
					/>
				) : (
					<FormikSelect
						name={path}
						label="Organisasjonsnummer"
						options={data}
						type="text"
						size="xlarge"
						isClearable={false}
					/>
				)
			}
		/>
	)
}
