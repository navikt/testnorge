import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import { UPDATE_TESTBRUKER } from '~/ducks/testBruker'
import RedigerTestbruker from './RedigerTestbruker'

// TODO: Reimplementere dette med nye testbruker state, inkludert sigrun
const mapStateToProps = (state, ownProps) => {
	return {
		testbruker:
			state.testbruker.items &&
			state.testbruker.items.find(item => item.ident === ownProps.match.params.ident)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		// getTestbruker: () => dispatch(GET_TESTBRUKERE([ownProps.match.params.ident])),
		updateTestbruker: userData => dispatch(UPDATE_TESTBRUKER(userData)),
		goBack: () => dispatch(push(`/gruppe/${ownProps.match.params.gruppeId}`))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTestbruker)
