import { connect } from 'react-redux'
import _orderBy from 'lodash/orderBy'
import GruppeOversikt from './GruppeOversikt'
import { getGrupper, settVisning, deleteGruppe } from '~/ducks/grupper'
import { setSort } from '~/ducks/sort'
import { showCreateOrEditGroup } from '~/ducks/gruppe'
import { addFavorite } from '~/ducks/bruker'

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
		gruppeListe: _orderBy(
			gruppeFiltering(state.grupper.items, state.search),
			gruppe => {
				const value = gruppe[state.sort.id]
				if (typeof value === 'string') return gruppe[state.sort.id].toLowerCase()

				return gruppe[state.sort.id]
			},
			state.sort.order
		),
		createOrUpdateId: state.gruppe.createOrUpdateId,
		grupper: state.grupper,
		error: state.grupper.error,
		sort: state.sort
	}
}

const mapDispatchToProps = dispatch => ({
	getGrupper: () => dispatch(getGrupper()),
	createGroup: () => dispatch(showCreateOrEditGroup(-1)),
	editGroup: editId => dispatch(showCreateOrEditGroup(editId)),
	settVisning: visning => dispatch(settVisning(visning)),
	deleteGruppe: gruppeId => dispatch(deleteGruppe(gruppeId)),
	setSort: sortObj => dispatch(setSort(sortObj)),
	addFavorite: groupId => dispatch(addFavorite(groupId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
