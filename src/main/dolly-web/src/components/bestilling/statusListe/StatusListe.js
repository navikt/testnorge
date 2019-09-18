import React from 'react'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingProgress from './BestillingProgress/BestillingProgress'
import MiljoeStatus from './MiljoeStatus/MiljoeStatus'

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

	const _onCloseMiljoeStatus = bestillingId => {
		removeNyBestillingStatus(bestillingId)
		getBestillinger()
	}

	if (isFetchingBestillinger) return false

	if (isCanceling) {
		return (
			<ContentContainer className="loading-content-container">
				<Loading label="AVBRYTER BESTILLING" />
			</ContentContainer>
		)
	}

	return nyeBestillinger.map(bestilling => {
		return (
			<div className="bestilling-status" key={bestilling.id}>
				{!bestilling.ferdig ? (
					<BestillingProgress
						bestilling={bestilling}
						getGruppe={getGruppe}
						getBestillinger={getBestillinger}
						cancelBestilling={cancelBestilling}
					/>
				) : (
					<MiljoeStatus bestilling={bestilling} onCloseButton={_onCloseMiljoeStatus} />
				)}
			</div>
		)
	})
}
