import { connect } from 'react-redux'
import GjenopprettBestillingComp from './GjenopprettBestilling'
import { getBestillinger, gjenopprettBestilling } from '~/ducks/bestillingStatus'

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId, id } = ownProps.bestilling
	return {
		gjenopprettBestilling: envs => dispatch(gjenopprettBestilling(id, envs)),
		getBestillinger: () => dispatch(getBestillinger(gruppeId))
	}
}

export default connect(null, mapDispatchToProps)(GjenopprettBestillingComp)
