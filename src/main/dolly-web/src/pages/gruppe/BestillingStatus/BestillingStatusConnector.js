import { connect } from 'react-redux'
import BestillingStatus from './BestillingStatus'
import { removeNyBestillingStatus, cancelBestilling } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(cancelBestilling)
const mapStateToProps = (state, ownProps) => {
	return {
		isCanceling: loadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const bestillingId = ownProps.bestilling.id
	return {
		removeNyBestillingStatus: bestillingsId => dispatch(removeNyBestillingStatus(bestillingsId)),
		cancelBestilling: () => dispatch(cancelBestilling(bestillingId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingStatus)
