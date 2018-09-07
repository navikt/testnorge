import { connect } from 'react-redux'
import GruppeOversikt from './GruppeOversikt'
import { getGrupper, settVisning, deleteGruppe } from '~/ducks/grupper'
import { showCreateOrEditGroup } from '~/ducks/gruppe'

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

const mapStateToProps = state => ({
	gruppeListe: gruppeFiltering(state.grupper.items, state.search),
	createOrUpdateId: state.gruppe.createOrUpdateId,
	grupper: state.grupper,
	error: state.grupper.error
})

const mapDispatchToProps = dispatch => ({
	getGrupper: () => dispatch(getGrupper()),
	createGroup: () => dispatch(showCreateOrEditGroup(-1)),
	editGroup: editId => dispatch(showCreateOrEditGroup(editId)),
	settVisning: visning => dispatch(settVisning(visning)),
	deleteGruppe: gruppeId => dispatch(deleteGruppe(gruppeId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
