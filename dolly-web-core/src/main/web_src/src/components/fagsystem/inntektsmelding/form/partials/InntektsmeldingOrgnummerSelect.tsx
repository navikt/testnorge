import * as React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

interface InntektsmeldingOrgnummerSelect {
	path: string
}

type Response = {
	orgnr: string
	enhetstype: string
	navn: string
	juridiskEnhet: string | null
}

type OrgOption = {
	lowercaseLabel?: string
	value: string
	label: string
	juridiskEnhet: string
}

export default ({ path }: InntektsmeldingOrgnummerSelect) => {
	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentOrgnr().then(
					({ liste }): Array<Response> =>
						liste
							.filter((org: Response) => org.juridiskEnhet)
							.map((org: Response) => ({
								value: org.orgnr,
								label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`
							}))
				)
			}
			render={(data: Array<OrgOption>) => (
				<FormikSelect
					name={path}
					label="Virksomhet (orgnr/id)"
					options={data}
					size="xlarge"
					isClearable={false}
				/>
			)}
		/>
	)
}
