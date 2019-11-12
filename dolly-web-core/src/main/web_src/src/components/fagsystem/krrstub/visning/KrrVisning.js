import React, { useEffect } from 'react'
import { useSelector } from 'react-redux'
import '~/components/fagsystem/fagsystemVisning/fagsystemVisning.less'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export default function KrrVisning(props) {
	const data = useSelector(state => state)
	const krrData = data.testbruker.items.krrstub && data.testbruker.items.krrstub[props.personId]

	if (!krrData) return null

	const { mobil, epost, reservert, gyldigFra, registrert } = krrData

	return (
		<div className="person-details-block">
			<h3>Kontaktinformasjon og reservasjon</h3>
			{props.isFetchingKrr && <Loading label="Henter data fra Krr" panel />}
			<div className="person-info-block">
				{mobil && (
					<div className="person-info-content">
						<h4>Mobilnummer</h4>
						<span>{mobil}</span>
					</div>
				)}
				{epost && (
					<div className="person-info-content">
						<h4>E-post</h4>
						<span>{epost}</span>
					</div>
				)}
				<div className="person-info-content">
					<h4>Reservert mot digitalkommunikasjon</h4>
					<span>{Formatters.oversettBoolean(reservert)}</span>
				</div>
				<div className="person-info-content">
					<h4>Gyldig fra</h4>
					<span>{Formatters.formatDate(gyldigFra)}</span>
				</div>
				<div className="person-info-content">
					<h4>Registrert i DKIF</h4>
					<span>{Formatters.oversettBoolean(registrert)}</span>
				</div>
			</div>
		</div>
	)
}
