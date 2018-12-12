import { connect } from 'react-redux'
import BestillingStatus from './BestillingStatus'
import {
	setBestillingStatus,
	miljoStatusSelector,
	cancelBestilling
} from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(cancelBestilling)
const mapStateToProps = (state, ownProps) => {
	const bestillingStatusObj = state.bestillingStatus[ownProps.bestilling.id]
	return {
		bestillingStatusObj,
		miljoeStatusObj: miljoStatusSelector(bestillingStatusObj),
		isCanceling: loadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const bestillingId = ownProps.bestilling.id
	return {
		setBestillingStatus: (id, data) => dispatch(setBestillingStatus(id, data)),
		cancelBestilling: () => dispatch(cancelBestilling(bestillingId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingStatus)
