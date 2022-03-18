import React from 'react'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingProgresjon from './BestillingProgresjon/BestillingProgresjon'
import BestillingResultat from './BestillingResultat/BestillingResultat'

export default function StatusListe({
	brukerBilde,
	brukerId,
	cancelBestilling,
	getBestillinger,
	getGruppe,
	getOrganisasjoner,
	isCanceling,
	isFetchingBestillinger,
	isFetchingOrgBestillinger,
	nyeBestillinger,
	removeNyBestillingStatus,
}) {
	const _onCloseBestillingResultat = (bestillingId) => {
		removeNyBestillingStatus(bestillingId)
		getBestillinger()
	}

	if (isFetchingBestillinger || isFetchingOrgBestillinger) return false

	if (isCanceling) {
		return (
			<ContentContainer className="loading-content-container">
				<Loading label="AVBRYTER BESTILLING" />
			</ContentContainer>
		)
	}

	if (sessionStorage.getItem('organisasjon_bestilling')) {
		nyeBestillinger.push(JSON.parse(sessionStorage.getItem('organisasjon_bestilling')))
	}

	return nyeBestillinger.map((bestilling) => {
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
						getOrganisasjoner={getOrganisasjoner}
						getBestillinger={getBestillinger}
						cancelBestilling={cancelBestilling}
						brukerId={brukerId}
					/>
				)}
			</div>
		)
	})
}
