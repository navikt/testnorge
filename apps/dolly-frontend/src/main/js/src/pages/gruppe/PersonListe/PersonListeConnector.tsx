import { connect } from 'react-redux'
import { actions as actionList, fetchPdlPersoner, fetchTpsfPersoner } from '@/ducks/fagsystem'
import { createLoadingSelector } from '@/ducks/loading'
import personListe from './PersonListe'

const loadingSelector = createLoadingSelector([
	actionList.getTpsf,
	actionList.getPDLPersoner,
	actionList.getPdlForvalter,
])

const mapStateToProps = (state) => ({
	search: state.search,
	fagsystem: state.fagsystem,
	isFetching: loadingSelector(state),
	sidetall: state.finnPerson.sidetall,
	visPerson: state.finnPerson.visPerson,
	hovedperson: state.finnPerson.hovedperson,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
	tmpPersoner: state.redigertePersoner,
	sorting: state.finnPerson.sorting,
})

const mapDispatchToProps = { fetchTpsfPersoner, fetchPdlPersoner }

export default connect(mapStateToProps, mapDispatchToProps)(personListe)
