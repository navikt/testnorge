import React from 'react'
import Status from './status/Status'

export default function StatusListe(props) {
	const { isFetchingBestillinger, nyeBestillinger, getGruppe, getBestillinger } = props

	if (isFetchingBestillinger) return false

	return nyeBestillinger.map(bestilling => (
		<Status
			key={bestilling.id}
			bestilling={bestilling}
			onIdenterUpdate={getGruppe}
			onBestillingerUpdate={getBestillinger}
		/>
	))
}
