import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'

type EgenOrganisasjonSelectProps = {
	name: string
	isClearable?: boolean
	onChange?: (selected: any) => void
}

export const EgenOrganisasjonSelect = ({
	name,
	isClearable = true,
	onChange = null,
}: EgenOrganisasjonSelectProps) => {
	return (
		<ErrorBoundary>
			<LoadableComponent
				onFetch={() =>
					SelectOptionsOppslag.hentVirksomheterFraOrgforvalter().then((response) =>
						response.map((virksomhet) => ({
							value: virksomhet.orgnummer,
							label: `${virksomhet.orgnummer} (${virksomhet.enhetstype}) - ${virksomhet.orgnavn}`,
						}))
					)
				}
				render={(data) => (
					<FormikSelect
						name={name}
						label="Organisasjonsnummer"
						options={data}
						size="xxlarge"
						isClearable={isClearable}
						onChange={onChange}
					/>
				)}
			/>
		</ErrorBoundary>
	)
}
