import { connect } from 'react-redux'
import { actions as actionList, fetchPdlPersoner, fetchTpsfPersoner } from '~/ducks/fagsystem'
import { actions } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import personListe from './PersonListe'

const loadingSelector = createLoadingSelector([
	actions.getById,
	actionList.getTpsf,
	actionList.getPdlForvalter,
	getBestillinger,
])

const mapStateToProps = (state, ownProps) => ({
	search: state.search,
	bestillingStatuser: state.bestillingStatuser,
	fagsystem: state.fagsystem,
	gruppeInfo: state.gruppe.gruppeInfo,
	identer: state.gruppe.ident,
	isFetching: loadingSelector(state),
	visPerson: state.finnPerson.visPerson,
})

const mapDispatchToProps = { fetchTpsfPersoner, fetchPdlPersoner }

export default connect(mapStateToProps, mapDispatchToProps)(personListe)
