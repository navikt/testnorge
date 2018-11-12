import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import { UPDATE_TESTBRUKER, GET_TPSF_TESTBRUKERE } from '~/ducks/testBruker'
import RedigerTestbruker from './RedigerTestbruker'

// TODO: Reimplementere dette med nye testbruker state, inkludert sigrunStub
const mapStateToProps = (state, ownProps) => {
	return {
		testbruker: state.testbruker.items.tpsf && state.testbruker.items.tpsf[0]
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getTestbruker: () => dispatch(GET_TPSF_TESTBRUKERE([ownProps.match.params.ident])),
		updateTestbruker: userData => dispatch(UPDATE_TESTBRUKER(userData)),
		goBack: () => dispatch(push(`/gruppe/${ownProps.match.params.gruppeId}`))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTestbruker)
