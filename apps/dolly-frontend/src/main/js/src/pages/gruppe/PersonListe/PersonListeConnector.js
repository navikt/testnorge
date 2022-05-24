import { connect } from 'react-redux'
import { actions as actionList, fetchPdlPersoner, fetchTpsfPersoner } from '~/ducks/fagsystem'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import personListe from './PersonListe'

const loadingSelector = createLoadingSelector([
	actionList.getTpsf,
	actionList.getPDLPersoner,
	actionList.getPdlForvalter,
	getBestillinger,
])

const mapStateToProps = (state) => ({
	search: state.search,
	bestillingStatuser: state.bestillingStatuser,
	fagsystem: state.fagsystem,
	isFetching: loadingSelector(state),
	sidetall: state.finnPerson.sidetall,
	visPerson: state.finnPerson.visPerson,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
	tmpPersoner: state.redigertePersoner,
})

const mapDispatchToProps = { fetchTpsfPersoner, fetchPdlPersoner }

export default connect(mapStateToProps, mapDispatchToProps)(personListe)
