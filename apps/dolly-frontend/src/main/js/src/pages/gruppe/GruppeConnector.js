import { connect } from 'react-redux'
import { actions, selectGruppeById } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { navigerTilPerson } from '~/ducks/finnPerson'
import { createLoadingSelector } from '~/ducks/loading'
import Gruppe from './Gruppe'

const loadingSelector = createLoadingSelector([actions.getById, getBestillinger])
const loadingSelectorSlettGruppe = createLoadingSelector(actions.remove)
const loadingSelectorSendTags = createLoadingSelector(actions.sendTags)
const loadingSelectorLaasGruppe = createLoadingSelector(actions.laas)
const loadingSelectorGetExcel = createLoadingSelector(actions.getGruppeExcelFil)

const mapStateToProps = (state, ownProps) => ({
	...ownProps,
	isFetching: loadingSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	isSendingTags: loadingSelectorSendTags(state),
	isLockingGruppe: loadingSelectorLaasGruppe(state),
	isFetchingExcel: loadingSelectorGetExcel(state),
	selectGruppe: selectGruppeById,
	grupper: state.gruppe,
	identer: state.gruppe.ident,
	brukernavn: state.bruker.brukerData.brukernavn,
	brukertype: state.bruker.brukerData.brukertype,
	bestillingStatuser: state.bestillingStatuser.byId,
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getGruppe: (gruppeId, pageNo, pageSize) => dispatch(actions.getById(gruppeId, pageNo, pageSize)),
	navigerTilPerson: (ident) => dispatch(navigerTilPerson(ident)),
	deleteGruppe: (gruppeId) => dispatch(actions.remove(gruppeId)),
	sendTags: (tags) => dispatch(actions.sendGruppeTags(gruppeId, tags)),
	laasGruppe: (gruppeId) =>
		dispatch(actions.laas(gruppeId, { erLaast: true, laastBeskrivelse: 'Låst gruppe' })),
	getBestillinger: (gruppeId) => dispatch(getBestillinger(gruppeId)),
	getGruppeExcelFil: (gruppeId) => dispatch(actions.getGruppeExcelFil(gruppeId)),
})

export default connect(mapStateToProps, mapDispatchToProps)(Gruppe)
