import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import Gruppe from './Gruppe'
import { getGruppe, showCreateOrEditGroup, deleteGruppe } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillinger'
import { getBestillingStatus } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import { resetSearch } from '~/ducks/search'
import bestillingStatus from '../../ducks/bestillingStatus'

const loadingSelector = createLoadingSelector(getGruppe)
const loadingBestillingerSelector = createLoadingSelector(getBestillinger)

const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	isFetchingBestillinger: loadingBestillingerSelector(state),
	gruppeArray: state.gruppe.data,
	createOrUpdateId: state.gruppe.createOrUpdateId,
	bestillinger: state.bestillinger
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const gruppeId = ownProps.match.params.gruppeId
	return {
		getGruppe: () => dispatch(getGruppe(gruppeId)),
		deleteGruppe: () => dispatch(deleteGruppe(gruppeId)),
		getBestillinger: () => dispatch(getBestillinger(gruppeId)),
		createGroup: () => dispatch(showCreateOrEditGroup(-1)),
		editTestbruker: ident => dispatch(push(`/gruppe/${gruppeId}/testbruker/${ident}`)),
		resetSearch: () => dispatch(resetSearch())
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
