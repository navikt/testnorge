import { connect } from 'react-redux'
import { getBestillinger, sokSelector } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import BestillingListe from './BestillingListe'

const loadingBestillingerSelector = createLoadingSelector(getBestillinger)

const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	isFetchingBestillinger: loadingBestillingerSelector(state),
	bestillinger: sokSelector(state, state.search),
	navigerBestillingId: state.finnPerson.visBestilling,
	sidetall: state.finnPerson.sidetall,
})

export default connect(mapStateToProps)(BestillingListe)
