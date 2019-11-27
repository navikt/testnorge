import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'

export const PdlfVisning = ({ data, loading }) => {
	if (!data) return false

	return (
		<div>
			<UtenlandsId data={data.utenlandskIdentifikasjonsnummer} loading={loading} />
			<FalskIdentitet data={data.falskIdentitet} />
			{/* IKKE FERDIG */}
		</div>
	)
}
