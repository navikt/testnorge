import { connect } from 'react-redux'
import BestillingStatus from './BestillingStatus'
import { setBestillingStatus } from '~/ducks/bestillingStatus'

const mapStateToProps = (state, ownProps) => ({
	bestillingStatusObj: state.bestillingStatus[ownProps.bestilling.id]
})

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		setBestillingStatus: (id, data) => dispatch(setBestillingStatus(id, data))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingStatus)
