import { connect } from 'react-redux'
import GruppeOversikt from './GruppeOversikt'
import {
	getGrupper,
	startRedigerGruppe,
	startOpprettGruppe,
	settVisning,
	deleteGruppe
} from '~/ducks/grupper'

const mapStateToProps = state => ({
	grupper: state.grupper,
	error: state.grupper.error
})

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
