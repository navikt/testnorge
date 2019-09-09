import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import Gruppe from './Gruppe'
import { getGruppe, showCreateOrEditGroup, deleteGruppe } from '~/ducks/gruppe'
import { getBestillinger, nyeBestillingerSelector } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import { resetSearch } from '~/ducks/search'

const loadingSelector = createLoadingSelector(getGruppe)
const loadingBestillingerSelector = createLoadingSelector(getBestillinger)
const loadingSelectorSlettGruppe = createLoadingSelector(deleteGruppe)

const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	isFetchingBestillinger: loadingBestillingerSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	gruppeArray: state.gruppe.data,
	createOrUpdateId: state.gruppe.createOrUpdateId,
	bestillinger: state.bestillingStatuser,
	nyeBestillinger: nyeBestillingerSelector(state.bestillingStatuser),
	openAm: state.openam
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const gruppeId = ownProps.match.params.gruppeId
	return {
		getGruppe: () => dispatch(getGruppe(gruppeId)),
		deleteGruppe: () => dispatch(deleteGruppe(gruppeId)),
		getBestillinger: () => dispatch(getBestillinger(gruppeId)),
		createGroup: () => dispatch(showCreateOrEditGroup(-1)),
		editTestbruker: (ident, dataSources) =>
			dispatch(push(`/gruppe/${gruppeId}/testbruker/${ident}${dataSources}`)),
		resetSearch: () => dispatch(resetSearch())
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
