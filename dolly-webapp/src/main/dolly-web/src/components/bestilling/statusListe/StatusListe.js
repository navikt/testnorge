import React from 'react'
import Status from './status/Status'

export default function StatusListe(props) {
	const {
		isFetchingBestillinger,
		nyeBestillinger,
		isCanceling,
		getGruppe,
		getBestillinger,
		removeNyBestillingStatus,
		cancelBestilling
	} = props

	if (isFetchingBestillinger) return false

	return nyeBestillinger.map(bestilling => (
		<Status
			key={bestilling.id}
			bestilling={bestilling}
			isCanceling={isCanceling}
			onIdenterUpdate={getGruppe}
			onBestillingerUpdate={getBestillinger}
			removeNyBestillingStatus={removeNyBestillingStatus}
			cancelBestilling={cancelBestilling}
		/>
	))
}
