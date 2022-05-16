import { connect } from 'react-redux'
import { actions, selectGruppeById } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Gruppe, { VisningType } from './Gruppe'
import { FormikProps } from 'formik'
import { Dispatch } from 'redux'
import { setVisning } from '~/ducks/finnPerson'

const loadingSelector = createLoadingSelector([actions.getById, getBestillinger])
const loadingSelectorSlettGruppe = createLoadingSelector(actions.remove)
const loadingSelectorSendTags = createLoadingSelector(actions.sendTags)
const loadingSelectorLaasGruppe = createLoadingSelector(actions.laas)
const loadingSelectorGetExcel = createLoadingSelector(actions.getGruppeExcelFil)

const mapStateToProps = (
	state: {
		gruppe: Object
		visning: string
		finnPerson: {
			visPerson: string
			visBestilling: string
			sidetall: number
			sideStoerrelse: number
			visning: string
		}
		bruker: { brukerData: { brukernavn: string; brukertype: string } }
		bestillingStatuser: { byId: number }
	},
	ownProps: FormikProps<any>
) => ({
	...ownProps,
	isFetching: loadingSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	isSendingTags: loadingSelectorSendTags(state),
	isLockingGruppe: loadingSelectorLaasGruppe(state),
	isFetchingExcel: loadingSelectorGetExcel(state),
	selectGruppe: selectGruppeById,
	grupper: state.gruppe,
	visning: state.finnPerson.visning,
	visPerson: state.finnPerson.visPerson,
	sidetall: state.finnPerson.sidetall,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
	brukernavn: state.bruker.brukerData.brukernavn,
	brukertype: state.bruker.brukerData.brukertype,
	bestillingStatuser: state.bestillingStatuser.byId,
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	getGruppe: (gruppeId: number, pageNo: number, pageSize: number) =>
		dispatch(actions.getById(gruppeId, pageNo, pageSize)),
	deleteGruppe: (gruppeId: number) => dispatch(actions.remove(gruppeId)),
	sendTags: (gruppeId: number, tags: string[]) => dispatch(actions.sendGruppeTags(gruppeId, tags)),
	laasGruppe: (gruppeId: number) =>
		dispatch(actions.laas(gruppeId, { erLaast: true, laastBeskrivelse: 'Låst gruppe' })),
	getBestillinger: (gruppeId: number) => dispatch(getBestillinger(gruppeId)),
	getGruppeExcelFil: (gruppeId: number) => dispatch(actions.getGruppeExcelFil(gruppeId)),
	setVisning: (visning: VisningType) => dispatch(setVisning(visning)),
})

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(Gruppe)
