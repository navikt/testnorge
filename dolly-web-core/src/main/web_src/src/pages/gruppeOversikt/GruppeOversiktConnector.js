import { connect } from 'react-redux'
import _orderBy from 'lodash/orderBy'
import GruppeOversikt from './GruppeOversikt'
import { setSort } from '~/ducks/sort'
import {
	listGrupper,
	settVisning,
	deleteGruppe,
	showCreateOrEditGroup,
	getGrupper,
	getGrupperByTeamId,
	getGrupperByUserId,
	sokSelectorOversikt
} from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector([getGrupper, getGrupperByTeamId, getGrupperByUserId])

const mapStateToProps = state => {
	return {
		searchActive: Boolean(state.search),
		isFetching: loadingSelector(state),
		gruppeListe: _orderBy(
			sokSelectorOversikt(state.gruppe.data, state.search),
			state.sort.id,
			state.sort.order
		),
		createOrUpdateId: state.gruppe.createOrUpdateId,
		visning: state.gruppe.visning,
		sort: state.sort
	}
}

const mapDispatchToProps = dispatch => ({
	listGrupper: () => dispatch(listGrupper()),
	createGroup: () => dispatch(showCreateOrEditGroup(-1)),
	editGroup: editId => dispatch(showCreateOrEditGroup(editId)),
	settVisning: visning => dispatch(settVisning(visning)),
	deleteGruppe: gruppeId => dispatch(deleteGruppe(gruppeId)),
	setSort: sortObj => dispatch(setSort(sortObj))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
