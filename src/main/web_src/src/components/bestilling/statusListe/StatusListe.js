import React from 'react'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingProgresjon from './BestillingProgresjon/BestillingProgresjon'
import BestillingResultat from './BestillingResultat/BestillingResultat'

export default function StatusListe(props) {
	const {
		isFetchingBestillinger,
		nyeBestillinger,
		isCanceling,
		brukerBilde,
		getGruppe,
		getBestillinger,
		removeNyBestillingStatus,
		cancelBestilling
	} = props

	const _onCloseBestillingResultat = bestillingId => {
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
				{bestilling.ferdig ? (
					<BestillingResultat
						bestilling={bestilling}
						onCloseButton={_onCloseBestillingResultat}
						brukerBilde={brukerBilde}
					/>
				) : (
					<BestillingProgresjon
						bestilling={bestilling}
						getGruppe={getGruppe}
						getBestillinger={getBestillinger}
						cancelBestilling={cancelBestilling}
					/>
				)}
			</div>
		)
	})
}
