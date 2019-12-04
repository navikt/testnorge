import { connect } from 'react-redux'
import _orderBy from 'lodash/orderBy'
import {
	actions,
	fetchMineGrupper,
	loadingGrupper,
	sokSelectorGruppeOversikt
} from '~/ducks/gruppe'
import GruppeOversikt from './GruppeOversikt'

const mapStateToProps = state => ({
	searchActive: Boolean(state.search),
	isFetching: loadingGrupper(state),
	mineIds: state.gruppe.mineIds,
	gruppeListe: _orderBy(sokSelectorGruppeOversikt(state), 'id', 'desc')
})

const mapDispatchToProps = {
	getGrupper: actions.getAlle,
	fetchMineGrupper
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
