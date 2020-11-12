import { connect } from 'react-redux'
import { actions } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import {
	getBestillinger,
	nyeBestillingerSelector,
	removeNyBestillingStatus,
	cancelBestilling
} from '~/ducks/bestillingStatus'
import StatusListe from './StatusListe'

const loadingCancelSelector = createLoadingSelector(cancelBestilling)
const loadingBestillingerSelector = createLoadingSelector(getBestillinger)

const mapStateToProps = state => ({
	isFetchingBestillinger: loadingBestillingerSelector(state),
	nyeBestillinger: nyeBestillingerSelector(state),
	isCanceling: loadingCancelSelector(state),
	brukerBilde: state.bruker.brukerBilde
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getGruppe: () => dispatch(actions.getById(ownProps.gruppeId)),
	getBestillinger: () => dispatch(getBestillinger(ownProps.gruppeId)),
	removeNyBestillingStatus: bestillingId => dispatch(removeNyBestillingStatus(bestillingId)),
	cancelBestilling: bestillingId => dispatch(cancelBestilling(bestillingId))
})

export default connect(mapStateToProps, mapDispatchToProps)(StatusListe)
