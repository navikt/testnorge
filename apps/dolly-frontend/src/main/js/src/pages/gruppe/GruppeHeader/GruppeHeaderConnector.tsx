import { connect, RootStateOrAny } from 'react-redux'
import { Action } from 'redux-actions'
import { actions } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import GruppeHeader from '~/pages/gruppe/GruppeHeader/GruppeHeader'

const loadingSelectorSlettGruppe = createLoadingSelector(actions.remove)
const loadingSelectorSendTags = createLoadingSelector(actions.sendTags)
const loadingSelectorLaasGruppe = createLoadingSelector(actions.laas)
const loadingSelectorGetExcel = createLoadingSelector(actions.getGruppeExcelFil)

const mapStateToProps = (state: RootStateOrAny) => ({
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	isSendingTags: loadingSelectorSendTags(state),
	isLockingGruppe: loadingSelectorLaasGruppe(state),
	isFetchingExcel: loadingSelectorGetExcel(state),
	antallPersonerFjernet: state.redigertePersoner.antallPersonerFjernet,
})

const mapDispatchToProps = (dispatch: (arg0: Action<any>) => any) => ({
	deleteGruppe: (gruppeId: number) => dispatch(actions.remove(gruppeId)),
	sendTags: (gruppeId: number, tags: string[]) => dispatch(actions.sendGruppeTags(gruppeId, tags)),
	laasGruppe: (gruppeId: number) =>
		dispatch(actions.laas(gruppeId, { erLaast: true, laastBeskrivelse: 'Låst gruppe' })),
	getGruppeExcelFil: (gruppeId: number) => dispatch(actions.getGruppeExcelFil(gruppeId)),
})

export default connect(mapStateToProps, mapDispatchToProps)(GruppeHeader)
