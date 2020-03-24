import React from 'react'
import _get from 'lodash/get'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const AaregOrgnummerSelect = ({ path }) => {
	const organisasjoner = []

	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentOrgnr().then(response => {
					response.liste.forEach(org => {
						org.juridiskEnhet &&
							organisasjoner.push({
								value: org.orgnr,
								label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`
							})
					})
					return organisasjoner
				})
			}
			render={data => (
				<FormikSelect
					name={path}
					label="Organisasjonsnummer"
					options={data}
					type="text"
					size="xlarge"
					isClearable={false}
				/>
			)}
		/>
	)
}
