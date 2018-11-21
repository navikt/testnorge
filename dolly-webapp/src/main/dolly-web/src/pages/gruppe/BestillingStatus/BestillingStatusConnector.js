import { connect } from 'react-redux'
import BestillingStatus from './BestillingStatus'
import { setBestillingStatus, miljoStatusSelector } from '~/ducks/bestillingStatus'

const mapStateToProps = (state, ownProps) => {
	const bestillingStatusObj = state.bestillingStatus[ownProps.bestilling.id]
	return {
		bestillingStatusObj,
		miljoeStatusObj: miljoStatusSelector(bestillingStatusObj)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		setBestillingStatus: (id, data) => dispatch(setBestillingStatus(id, data))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingStatus)
