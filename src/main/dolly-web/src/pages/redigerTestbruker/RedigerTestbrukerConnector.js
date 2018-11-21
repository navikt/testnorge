import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import {
	UPDATE_TESTBRUKER,
	GET_TPSF_TESTBRUKERE,
	findEnvironmentsForIdent
} from '~/ducks/testBruker'
import { getGruppe } from '~/ducks/gruppe'
import RedigerTestbruker from './RedigerTestbruker'

// TODO: Reimplementere dette med nye testbruker state, inkludert sigrunStub
const mapStateToProps = (state, ownProps) => {
	return {
		testbruker: state.testbruker.items.tpsf && state.testbruker.items.tpsf[0],
		testbrukerEnvironments: findEnvironmentsForIdent(state, ownProps.match.params.ident)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getTestbruker: () => dispatch(GET_TPSF_TESTBRUKERE([ownProps.match.params.ident])),
		getGruppe: () => dispatch(getGruppe(ownProps.match.params.gruppeId)),
		updateTestbruker: (userData, tpsData) => dispatch(UPDATE_TESTBRUKER(userData, tpsData)),
		goBack: () => dispatch(push(`/gruppe/${ownProps.match.params.gruppeId}`))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTestbruker)
