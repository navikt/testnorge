import { connect } from 'react-redux'
import PersonDetaljer from './PersonDetaljer'
import DataMapper from '~/service/dataMapper'
import { GET_KRR_TESTBRUKER, GET_SIGRUN_TESTBRUKER } from '~/ducks/testBruker'
import { FRIGJOER_TESTBRUKER } from '~/ducks/testBruker'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelectorKrr = createLoadingSelector(GET_KRR_TESTBRUKER)
const loadingSelectorSigrun = createLoadingSelector(GET_SIGRUN_TESTBRUKER)

const mapStateToProps = (state, ownProps) => ({
	isFetchingKrr: loadingSelectorKrr(state),
	isFetchingSigrun: loadingSelectorSigrun(state),
	personData: DataMapper.getDetailedData(state, ownProps)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getKrrTestbruker: () => dispatch(GET_KRR_TESTBRUKER(ownProps.personId)),
	getSigrunTestbruker: () => dispatch(GET_SIGRUN_TESTBRUKER(ownProps.personId)),
	frigjoerTestbruker: () => dispatch(FRIGJOER_TESTBRUKER(ownProps.personId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(PersonDetaljer)
