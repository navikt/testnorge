import { connect } from 'react-redux'
import GruppeOversikt from './GruppeOversikt'
import {
	getGrupper,
	startRedigerGruppe,
	startOpprettGruppe,
	settVisning,
	deleteGruppe
} from '~/ducks/grupper'

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
		gruppeListe: gruppeFiltering(state.grupper.items, state.search),
		grupper: state.grupper,
		error: state.grupper.error
	}
}

const mapDispatchToProps = dispatch => ({
	getGrupper: () => dispatch(getGrupper()),
	startRedigerGruppe: editId => dispatch(startRedigerGruppe(editId)),
	startOpprettGruppe: () => dispatch(startOpprettGruppe()),
	settVisning: visning => dispatch(settVisning(visning)),
	deleteGruppe: gruppeId => dispatch(deleteGruppe(gruppeId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
