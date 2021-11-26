import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const UtenlandsId = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<div>
			<SubOverskrift label="Utenlandsk identifikasjonsnummer" iconKind="identifikasjon" />
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Identifikasjonsnummer" value={id.identifikasjonsnummer} />
							<TitleValue
								title="Utstederland"
								value={id.utstederland}
								kodeverk={AdresseKodeverk.Utstederland}
							/>
							<TitleValue
								title="Opphørt"
								value={Formatters.oversettBoolean(Boolean(id.opphoert))}
							/>
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
