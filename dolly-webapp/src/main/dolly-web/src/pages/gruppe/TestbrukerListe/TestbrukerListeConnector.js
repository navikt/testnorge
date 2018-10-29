import { connect } from 'react-redux'
import TestbrukerListe from './TestbrukerListe'
import { GET_TESTBRUKERE, sokSelector } from '~/ducks/testBruker'
import DataMapper from '~/service/dataMapper'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(GET_TESTBRUKERE)
const mapStateToProps = state => ({
	searchActive: Boolean(state.search),
	headers: DataMapper.getHeaders(),
	testbrukere: sokSelector(DataMapper.getData(state), state.search),
	isFetching: loadingSelector(state)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getTestbrukere: () => dispatch(GET_TESTBRUKERE(ownProps.testidenter.map(ident => ident.ident)))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TestbrukerListe)
