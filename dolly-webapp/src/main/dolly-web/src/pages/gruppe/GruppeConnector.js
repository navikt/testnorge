import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import Gruppe from './Gruppe'
import { getGruppe, showCreateOrEditGroup, deleteGruppe } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import { resetSearch } from '~/ducks/search'

const loadingSelector = createLoadingSelector(getGruppe)
const loadingSelectorSlettGruppe = createLoadingSelector(deleteGruppe)

const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	gruppeArray: state.gruppe.data,
	createOrUpdateId: state.gruppe.createOrUpdateId,
	antallBestillinger: state.bestillingStatuser.data.length,
	openAm: state.openam
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId } = ownProps.match.params
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
