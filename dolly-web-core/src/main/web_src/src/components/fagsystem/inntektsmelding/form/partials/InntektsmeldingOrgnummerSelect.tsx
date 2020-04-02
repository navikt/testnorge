import * as React from 'react'
import { get as _get } from 'lodash'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikProps } from 'formik'

interface InntektsmeldingOrgnummerSelect {
	path: string
	formikBag: FormikProps<{ setFieldValue: () => void }>
}

type OrgOption = {
	lowercaseLabel?: string
	value: string
	label: string
	juridiskEnhet: string
}

export default ({ path, formikBag }: InntektsmeldingOrgnummerSelect) => {
	const setOrgnr = (org: OrgOption) => {
		formikBag.setFieldValue(`${path}.virksomhetsnummer`, org.juridiskEnhet)
		formikBag.setFieldValue(`${path}.orgnr`, org.value) //orgnr ignoreres av backend. Sendes for å kunne bruke nedtrekksliste
	}

	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentOrgnr().then(({ liste }: any) =>
					liste
						.filter((org: any) => org.juridiskEnhet)
						.map((org: any) => ({
							value: org.orgnr,
							label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`,
							juridiskEnhet: org.juridiskEnhet
						}))
				)
			}
			render={(data: Array<OrgOption>) => (
				<DollySelect
					name={path}
					label="Virksomhet (orgnr/id)"
					options={data}
					size="xlarge"
					onChange={setOrgnr}
					value={_get(formikBag.values, `${path}.orgnr`)}
					feil={
						_get(formikBag.values, `${path}.orgnr`) === '' && {
							feilmelding: 'Feltet er påkrevd'
						}
					}
					isClearable={false}
				/>
			)}
		/>
	)
}
