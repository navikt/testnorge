import { connect } from 'react-redux'
import { getBestillinger, sokSelector } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import BestillingListe from './BestillingListe'
import { NAVIGER_BESTILLING_ID } from '~/pages/gruppe/PersonVisning/TidligereBestillinger/TidligereBestillinger'

const loadingBestillingerSelector = createLoadingSelector(getBestillinger)

const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	isFetchingBestillinger: loadingBestillingerSelector(state),
	bestillinger: sokSelector(state, state.search),
	navigerBestillingId: sessionStorage.getItem(NAVIGER_BESTILLING_ID),
})

export default connect(mapStateToProps)(BestillingListe)
