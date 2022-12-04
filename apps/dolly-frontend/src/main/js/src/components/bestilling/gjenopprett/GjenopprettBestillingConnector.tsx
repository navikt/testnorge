import { connect } from 'react-redux'
import GjenopprettBestillingComp from './GjenopprettBestilling'
import { gjenopprettBestilling, gjenopprettOrganisasjonBestilling } from '~/ducks/bestillingStatus'

const mapDispatchToProps = (dispatch, ownProps) => {
	const { id } = ownProps.bestilling
	return {
		gjenopprettBestilling: (envs) => dispatch(gjenopprettBestilling(id, envs)),
		gjenopprettOrganisasjonBestilling: (envs) =>
			dispatch(gjenopprettOrganisasjonBestilling(id, envs)),
	}
}

export default connect(null, mapDispatchToProps)(GjenopprettBestillingComp)
