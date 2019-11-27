import React from 'react'
import UtenlandsId from './partials/UtenlandsId'

export const PdlfVisning = ({ data, loading }) => {
	if (!data) return false

	return (
		<div>
			<UtenlandsId data={data.utenlandskIdentifikasjonsnummer} loading={loading} />
			{/* IKKE FERDIG */}
		</div>
	)
}
