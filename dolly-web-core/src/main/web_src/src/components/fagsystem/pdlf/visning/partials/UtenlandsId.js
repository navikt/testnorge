import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const UtenlandsId = ({ data, loading }) => {
	if (loading) return <Loading label="laster PDL-data" />
	if (!data) return false

	//IKKE FERDIG
	return (
		<div>
			<SubOverskrift label="Utenlandsk identifikasjonsnummer" />
			<div className="person-visning_content">
				{data.map((id, idx) => (
					<div>
						<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
						<TitleValue title="Identifikasjonsnummer" value={id.idnummer} />
						<TitleValue title="Kilde" value={id.kilde} />
						<TitleValue title="Opphørt" value={Formatters.oversettBoolean(id.opphoert)} />
						<TitleValue title="Utstederland" value={id.utstederland} kodeverk="Landkoder" />
					</div>
				))}
			</div>
		</div>
	)
}
