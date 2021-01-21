import { connect } from 'react-redux'
import { actions } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import {
	getBestillinger,
	nyeBestillingerSelector,
	getOrganisasjonBestilling,
	removeNyBestillingStatus,
	cancelBestilling
} from '~/ducks/bestillingStatus'
import StatusListe from './StatusListe'

const loadingCancelSelector = createLoadingSelector(cancelBestilling)
const loadingBestillingerSelector = createLoadingSelector(getBestillinger)
const loadingOrgBestillingSelector = createLoadingSelector(getOrganisasjonBestilling)

const mapStateToProps = state => {
	return {
		isFetchingBestillinger: loadingBestillingerSelector(state),
		isFetchingOrgBestillinger: loadingOrgBestillingSelector(state),
		nyeBestillinger: nyeBestillingerSelector(state),
		isCanceling: loadingCancelSelector(state),
		brukerBilde: state.bruker.brukerBilde
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	console.log('ownProps :>> ', ownProps)
	return {
		getGruppe: () => dispatch(actions.getById(ownProps.gruppeId)),
		getBestillinger: () =>
			ownProps.brukerId
				? dispatch(getOrganisasjonBestilling(ownProps.brukerId))
				: dispatch(getBestillinger(ownProps.gruppeId)),
		removeNyBestillingStatus: bestillingId => dispatch(removeNyBestillingStatus(bestillingId)),
		cancelBestilling: bestillingId => dispatch(cancelBestilling(bestillingId))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(StatusListe)
