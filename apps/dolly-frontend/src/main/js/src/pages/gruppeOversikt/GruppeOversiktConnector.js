import { connect } from 'react-redux'
import _orderBy from 'lodash/orderBy'
import {
	actions,
	fetchMineGrupper,
	loadingGrupper,
	sokSelectorGruppeOversikt,
} from '~/ducks/gruppe'
import { navigerTilBestilling, navigerTilPerson } from '~/ducks/finnPerson'
import GruppeOversikt from './GruppeOversikt'

const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	isFetching: loadingGrupper(state),
	mineIds: state.gruppe.mineIds,
	gruppeListe: _orderBy(sokSelectorGruppeOversikt(state), 'id', 'desc'),
	gruppeInfo: state.gruppe.gruppeInfo,
	importerteZIdenter: state.gruppe.importerteZIdenter,
	brukerProfil: ownProps?.brukerProfil,
})

const mapDispatchToProps = {
	getGrupper: actions.getAlle,
	navigerTilPerson: navigerTilPerson,
	navigerTilBestilling: navigerTilBestilling,
	fetchMineGrupper,
}

export default connect(mapStateToProps, mapDispatchToProps)(GruppeOversikt)
