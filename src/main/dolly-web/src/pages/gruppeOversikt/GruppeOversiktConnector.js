import { connect } from 'react-redux'
import _orderBy from 'lodash/orderBy'
import GruppeOversikt from './GruppeOversikt'
import {
	getGrupper,
	fetchGrupperTilBruker,
	getGrupperByUserId,
	sokSelectorOversikt
} from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector([getGrupper, getGrupperByUserId])

const mapStateToProps = state => ({
	searchActive: Boolean(state.search),
	isFetching: loadingSelector(state),
	gruppeListe: _orderBy(sokSelectorOversikt(state.gruppe.data, state.search), 'id', 'desc')
})

const mapDispatchToProps = dispatch => ({
	getGrupper: () => dispatch(getGrupper()),
	getMineGrupper: () => dispatch(fetchGrupperTilBruker())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
