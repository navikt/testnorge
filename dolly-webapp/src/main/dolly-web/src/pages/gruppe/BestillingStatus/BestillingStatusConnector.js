import { connect } from 'react-redux'
import BestillingStatus from './BestillingStatus'
import {
	setBestillingStatus,
	miljoStatusSelector,
	cancelBestilling
} from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import _find from 'lodash/find'

const loadingSelector = createLoadingSelector(cancelBestilling)
const mapStateToProps = (state, ownProps) => {
	const bestillinger = state.bestillingStatuser.data

	const bestilling = _find(bestillinger, bestilling => bestilling.id === ownProps.bestillingsId)

	console.log(bestilling, 'besObject')
	return {
		bestilling,
		miljoeStatusObj: miljoStatusSelector(bestilling),
		isCanceling: loadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const bestillingId = ownProps.bestillingsId
	return {
		setBestillingStatus: (id, data) => dispatch(setBestillingStatus(id, data)),
		cancelBestilling: () => dispatch(cancelBestilling(bestillingId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingStatus)
