import { connect } from 'react-redux'
import { actions } from '~/ducks/gruppe'
import { fetchOrganisasjoner } from '~/ducks/organisasjon'
import { createLoadingSelector } from '~/ducks/loading'
import {
	cancelBestilling,
	getBestillinger,
	getOrganisasjonBestilling,
	nyeBestillingerSelector,
	removeNyBestillingStatus,
} from '~/ducks/bestillingStatus'
import StatusListe from './StatusListe'

const loadingCancelSelector = createLoadingSelector(cancelBestilling)
const loadingBestillingerSelector = createLoadingSelector(getBestillinger)
const loadingOrgBestillingSelector = createLoadingSelector(getOrganisasjonBestilling)

const mapStateToProps = (state) => ({
	isFetchingBestillinger: loadingBestillingerSelector(state),
	isFetchingOrgBestillinger: loadingOrgBestillingSelector(state),
	nyeBestillinger: nyeBestillingerSelector(state),
	isCanceling: loadingCancelSelector(state),
})

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getGruppe: (pageNo, pageSize) =>
			ownProps.gruppeId && dispatch(actions.getById(ownProps.gruppeId, pageNo, pageSize)),
		getOrganisasjoner: fetchOrganisasjoner(dispatch),
		getBestillinger: () =>
			ownProps.brukerId
				? dispatch(getOrganisasjonBestilling(ownProps.brukerId))
				: dispatch(getBestillinger(ownProps.gruppeId)),
		removeNyBestillingStatus: (bestillingId) => dispatch(removeNyBestillingStatus(bestillingId)),
		cancelBestilling: (bestillingId) => dispatch(cancelBestilling(bestillingId)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(StatusListe)
