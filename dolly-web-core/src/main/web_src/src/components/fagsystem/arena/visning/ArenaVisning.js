import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const ArenaVisning = ({ data, bestData, loading }) => {
	if (loading) return <Loading label="Laster arena-data" />
	if (!data) return false

	// Areneforvalternen returnerer veldig lite informasjon, bruker derfor data fra bestillingen i tillegg
	const brukertype = data.arbeidsokerList[0].servicebehov
	const { kvalifiseringsgruppe, inaktiveringDato, aap115, aap } = bestData

	return (
		<div>
			<SubOverskrift label="Arena" />
			<div className="person-visning_content">
				<TitleValue title="Brukertype" value={Formatters.booleanToServicebehov(brukertype)} />
				{kvalifiseringsgruppe && (
					<TitleValue
						title="Servicebehov"
						value={Formatters.servicebehovKodeTilBeskrivelse(kvalifiseringsgruppe)}
					/>
				)}
				{inaktiveringDato && (
					<TitleValue title="Inaktiv fra dato" value={Formatters.formatDate(inaktiveringDato)} />
				)}
				{aap115 && <TitleValue title="Har 11-5-vedtak" value="Ja" />}
				{aap115 && <TitleValue title="Fra dato" value={Formatters.formatDate(aap115[0].fraDato)} />}
				{aap && <TitleValue title="Har AAP vedtak UA - positivt utfall" value="Ja" />}
				{aap && <TitleValue title="Fra dato" value={Formatters.formatDate(aap[0].fraDato)} />}
				{aap && <TitleValue title="Til dato" value={Formatters.formatDate(aap[0].tilDato)} />}
			</div>
		</div>
	)
}
