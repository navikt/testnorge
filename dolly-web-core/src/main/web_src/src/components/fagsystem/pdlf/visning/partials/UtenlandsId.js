import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const UtenlandsId = ({ data, loading }) => {
	if (loading) return <Loading label="laster PDL-data" />
	if (!data) return false

	console.log('data :', data)
	return (
		<div>
			<SubOverskrift label="Utenlandsk identifikasjonsnummer" />
			<div className="person-visning_content">
				{data.map((id, idx) => (
					<div key={id.opplysningsId}>
						<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
						<TitleValue title="Identifikasjonsnummer" value={id.idNummer} />
						<TitleValue title="Kilde" value={id.kilde} />
						<TitleValue
							title="Opphørt"
							value={Formatters.oversettBoolean(Boolean(id.registrertINAV))}
						/>
						<TitleValue title="Utstederland" value={id.utstederland} kodeverk="Landkoder" />
					</div>
				))}
			</div>
		</div>
	)
}
