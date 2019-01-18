import { connect } from 'react-redux'
import BestillingStatus from './BestillingStatus'
import {
	removeNyBestillingStatus,
	miljoStatusSelector,
	cancelBestilling
} from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import _find from 'lodash/find'

const loadingSelector = createLoadingSelector(cancelBestilling)
const mapStateToProps = (state, ownProps) => {
	console.log(ownProps.bestilling, 'bestili')
	return {
		miljoeStatusObj: miljoStatusSelector(ownProps.bestilling),
		isCanceling: loadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const bestillingId = ownProps.bestillingsId
	return {
		removeNyBestillingStatus: bestillingsId => dispatch(removeNyBestillingStatus(bestillingsId)),
		cancelBestilling: () => dispatch(cancelBestilling(bestillingId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingStatus)
