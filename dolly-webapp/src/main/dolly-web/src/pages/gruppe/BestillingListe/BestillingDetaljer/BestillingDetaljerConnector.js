import { connect } from 'react-redux'
import BestillingDetaljer from './BestillingDetaljer'
import {
	getBestillinger,
	miljoStatusSelector,
	gjenopprettBestilling
} from '~/ducks/bestillingStatus'

const mapStateToProps = (state, ownProps) => {
	return {
		openAmState: state.openam,
		openAm: ownProps.bestilling.openamSent
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const bestillingId = ownProps.bestilling.id
	const gruppeId = ownProps.bestilling.gruppeId
	return {
		gjenopprettBestilling: envs => dispatch(gjenopprettBestilling(bestillingId, envs)),
		getBestillinger: () => dispatch(getBestillinger(gruppeId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(BestillingDetaljer)
