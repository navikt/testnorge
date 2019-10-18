import { connect } from 'react-redux'
import _get from 'lodash/get'
import { fetchTpsfTestbrukere, GET_TPSF_TESTBRUKERE, sokSelector } from '~/ducks/testBruker'
import DataMapper from '~/service/dataMapper'
import { createLoadingSelector } from '~/ducks/loading'
import TestbrukerListe from './TestbrukerListe'

const loadingSelector = createLoadingSelector(GET_TPSF_TESTBRUKERE)
const mapStateToProps = state => ({
	searchActive: Boolean(state.search),
	testbrukerListe: sokSelector(DataMapper.getData(state), state.search),
	isFetching: loadingSelector(state)
})

const mapDispatchToProps = dispatch => ({
	getTPSFTestbrukere: () => dispatch(fetchTpsfTestbrukere())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TestbrukerListe)
