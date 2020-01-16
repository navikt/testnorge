import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const UtenlandsId = ({ data, loading }) => {
	if (loading) return <Loading label="laster PDL-data" />
	if (!data || data.length === 0) return false

	return (
		<div>
			<SubOverskrift label="Utenlandsk identifikasjonsnummer" iconKind="identifikasjon" />
			<DollyFieldArray data={data} nested>
				{(id, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Identifikasjonsnummer" value={id.identifikasjonsnummer} />
						{id.metadata && id.metadata.endringer && (
							<TitleValue title="Kilde" value={id.metadata.endringer[0].kilde} />
						)}
						<TitleValue title="Opphørt" value={Formatters.oversettBoolean(Boolean(id.opphoert))} />
						<TitleValue title="Utstederland" value={id.utstederland} kodeverk="Landkoder" />
					</div>
				)}
			</DollyFieldArray>
		</div>
	)
}
