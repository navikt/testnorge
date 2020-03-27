import React from 'react'
import _get from 'lodash/get'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const AaregOrgnummerSelect = ({ path }) => (
	<LoadableComponent
		onFetch={() =>
			SelectOptionsOppslag.hentOrgnr().then(({ liste }) =>
				liste
					.filter(org => org.juridiskEnhet)
					.map(org => ({
						value: org.orgnr,
						label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`
					}))
			)
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
