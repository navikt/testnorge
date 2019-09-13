import { connect } from 'react-redux'
import { getEnvironments } from '~/ducks/environments'
import { getBestillinger, sokSelector } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import BestillingListe from './BestillingListe'

const loadingBestillingerSelector = createLoadingSelector(getBestillinger)

const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	isFetchingBestillinger: loadingBestillingerSelector(state),
	bestillinger: sokSelector(state.bestillingStatuser.data, state.search)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getEnvironments: () => dispatch(getEnvironments())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingListe)
