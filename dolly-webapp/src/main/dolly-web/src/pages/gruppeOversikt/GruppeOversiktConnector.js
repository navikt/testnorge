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
	getGrupperByUserId
} from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(getGrupper, getGrupperByTeamId, getGrupperByUserId)

const gruppeFiltering = (items, searchText) => {
	if (!items) return null

	if (!searchText) return items

	const query = searchText.toLowerCase()
	return items.filter(item => {
		if (item.navn.toLowerCase().includes(query)) return true
		if (item.team.navn.toLowerCase().includes(query)) return true

		return false
	})
}

const mapStateToProps = state => {
	return {
		isFetching: loadingSelector(state),
		gruppeListe: _orderBy(
			gruppeFiltering(state.gruppe.data, state.search),
			gruppe => {
				const value = gruppe[state.sort.id]
				if (typeof value === 'string') return gruppe[state.sort.id].toLowerCase()

				return gruppe[state.sort.id]
			},
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
