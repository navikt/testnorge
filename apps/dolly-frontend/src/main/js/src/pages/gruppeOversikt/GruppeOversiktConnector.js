import { connect } from 'react-redux'
import { actions, fetchMineGrupper, loadingGrupper } from '~/ducks/gruppe'
import GruppeOversikt from './GruppeOversikt'

const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	isFetching: loadingGrupper(state),
	gruppeInfo: state.gruppe.gruppeInfo,
	importerteZIdenter: state.gruppe.importerteZIdenter,
	sidetall: state.finnPerson.sidetall,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
})

const mapDispatchToProps = {
	getGrupper: actions.getAlle,
	fetchMineGrupper,
}

export default connect(mapStateToProps, mapDispatchToProps)(GruppeOversikt)
