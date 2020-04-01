import * as React from 'react'
import { get as _get } from 'lodash'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

interface InntektsmeldingOrgnummerSelect {
	path: string
}

export default ({ path }: InntektsmeldingOrgnummerSelect) => (
	<LoadableComponent
		onFetch={() =>
			SelectOptionsOppslag.hentOrgnr().then(({ liste }: any) =>
				liste
					.filter((org: any) => org.juridiskEnhet)
					.map((org: any) => ({
						value: org.orgnr,
						label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`
					}))
			)
		}
		render={(data: any) => (
			<FormikSelect
				name={path}
				label="Virksomhet (orgnr/id)"
				options={data}
				type="text"
				size="xlarge"
				isClearable={false}
				visHvisAvhuket
			/>
		)}
	/>
)
