import { connect } from 'react-redux'
import TestbrukerListe from './TestbrukerListe'
import { GET_TESTBRUKERE } from '~/ducks/testBruker'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(GET_TESTBRUKERE)
const mapStateToProps = state => ({
	testbrukere: state.testbruker.items,
	isFetching: loadingSelector(state)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getTestbrukere: () => dispatch(GET_TESTBRUKERE(ownProps.testidenter.map(ident => ident.ident)))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TestbrukerListe)
