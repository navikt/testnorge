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
	isFetching: loadingSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	isSendingTags: loadingSelectorSendTags(state),
	isLockingGruppe: loadingSelectorLaasGruppe(state),
	isFetchingExcel: loadingSelectorGetExcel(state),
	gruppe: selectGruppeById(state, ownProps.match.params.gruppeId),
	identer: state.gruppe.ident,
	brukernavn: state.bruker.brukerData.brukernavn,
	bestillingStatuser: state.bestillingStatuser.byId,
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId } = ownProps.match.params
	return {
		getGruppe: (pageNo, pageSize) => dispatch(actions.getById(gruppeId, pageNo, pageSize)),
		navigerTilPerson: (ident) => dispatch(navigerTilPerson(ident)),
		deleteGruppe: () => dispatch(actions.remove(gruppeId)),
		sendTags: (tags) => dispatch(actions.sendGruppeTags(gruppeId, tags)),
		laasGruppe: () =>
			dispatch(actions.laas(gruppeId, { erLaast: true, laastBeskrivelse: 'Låst gruppe' })),
		getBestillinger: () => dispatch(getBestillinger(gruppeId)),
		getGruppeExcelFil: () => dispatch(actions.getGruppeExcelFil(gruppeId)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Gruppe)
