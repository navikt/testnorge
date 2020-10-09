import { connect } from 'react-redux'
import { fetchMineGrupper } from '~/ducks/gruppe'
import { actions } from '~/ducks/gruppe'
import ImporterGrupper from './ImporterGrupper'

const mapStateToProps = (state, ownProps) => {
	// console.log('state :>> ', state)
	return {
		importerteZIdenter: state.gruppe.importerteZIdenter
	}
}
const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		importZIdent: ZIdent => dispatch(actions.importZIdent(ZIdent)),
		fetchMineGrupper
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(ImporterGrupper)
