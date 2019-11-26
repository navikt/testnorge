import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const KrrVisning = ({ data, loading }) => {
	if (loading) return <Loading label="laster krr data" />
	if (!data) return false

	return (
		<div>
			<SubOverskrift label="Kontaktinformasjon og reservasjon" />
			<div className="person-visning_content">
				<TitleValue title="Mobilnummer" value={data.mobil} />
				<TitleValue title="E-post" value={data.epost} />
				<TitleValue
					title="Reservert mot digitalkommunikasjon"
					value={Formatters.oversettBoolean(data.reservert)}
				/>
				<TitleValue title="Gyldig fra" value={Formatters.formatDate(data.gyldigFra)} />
				<TitleValue title="Registrert i DKIF" value={Formatters.oversettBoolean(data.registrert)} />
			</div>
		</div>
	)
}
