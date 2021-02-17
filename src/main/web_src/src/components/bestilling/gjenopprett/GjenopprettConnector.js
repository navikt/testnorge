import { connect } from 'react-redux'
import GjenopprettBestillingComp from './Gjenopprett'
import {
	getBestillinger,
	gjenopprettBestilling,
	getOrganisasjonBestilling,
	gjenopprettOrganisasjonBestilling
} from '~/ducks/bestillingStatus'

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId, id } = ownProps.bestilling
	return {
		gjenopprettBestilling: envs => dispatch(gjenopprettBestilling(id, envs)),
		gjenopprettOrganisasjonBestilling: envs =>
			dispatch(gjenopprettOrganisasjonBestilling(id, envs)),
		getBestillinger: () =>
			ownProps.brukerId
				? dispatch(getOrganisasjonBestilling(ownProps.brukerId))
				: dispatch(getBestillinger(gruppeId))
	}
}

export default connect(null, mapDispatchToProps)(GjenopprettBestillingComp)
