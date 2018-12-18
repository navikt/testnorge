import { connect } from 'react-redux'
import TestbrukerListe from './TestbrukerListe'
import { GET_TPSF_TESTBRUKERE, sokSelector } from '~/ducks/testBruker'
import DataMapper from '~/service/dataMapper'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(GET_TPSF_TESTBRUKERE)
const mapStateToProps = state => ({
	searchActive: Boolean(state.search),
	headers: DataMapper.getHeaders(),
	testbrukerListe: sokSelector(DataMapper.getData(state), state.search),
	isFetching: loadingSelector(state),
	username: state.bruker.brukerData.navIdent
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getTPSFTestbrukere: () =>
		dispatch(GET_TPSF_TESTBRUKERE(ownProps.testidenter.map(ident => ident.ident)))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TestbrukerListe)
