import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { Historikk } from '~/components/ui/historikk/Historikk'

export const Visning = ({ data }) => {
	return (
		<>
			<TitleValue title="Mobilnummer" value={data.mobil} />
			<TitleValue title="E-post" value={data.epost} />
			<TitleValue
				title="Språk"
				value={data.spraak && Formatters.showLabel('spraaktype', data.spraak.replace(' ', ''))}
			/>
			<TitleValue
				title="Reservert mot digitalkommunikasjon"
				value={Formatters.oversettBoolean(data.reservert)}
			/>
			<TitleValue title="Gyldig fra" value={Formatters.formatDate(data.gyldigFra)} />
			<TitleValue title="Registrert i DKIF" value={Formatters.oversettBoolean(data.registrert)} />
		</>
	)
}

export const KrrVisning = ({ data, loading }) => {
	if (loading) return <Loading label="laster krr data" />
	if (!data) return false

	const sortedData = Array.isArray(data) ? data.slice().reverse() : data

	return (
		<div>
			<SubOverskrift label="Kontaktinformasjon og reservasjon" iconKind="krr" />
			<div className="person-visning_content">
				<Historikk component={Visning} data={sortedData} />
			</div>
		</div>
	)
}
