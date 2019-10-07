import { connect } from 'react-redux'
import _orderBy from 'lodash/orderBy'
import GruppeOversikt from './GruppeOversikt'
import {
	listGrupper,
	settVisning,
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
		gruppeListe: _orderBy(sokSelectorOversikt(state.gruppe.data, state.search), 'id', 'desc'),
		visning: state.gruppe.visning
	}
}

const mapDispatchToProps = dispatch => ({
	listGrupper: () => dispatch(listGrupper()),
	settVisning: visning => dispatch(settVisning(visning))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
