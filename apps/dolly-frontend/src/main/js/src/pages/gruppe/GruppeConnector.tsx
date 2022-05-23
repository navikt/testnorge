import { connect } from 'react-redux'
import { actions, selectGruppeById } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Gruppe, { VisningType } from './Gruppe'
import { FormikProps } from 'formik'
import { Dispatch } from 'redux'
import { setVisning } from '~/ducks/finnPerson'

const loadingSelector = createLoadingSelector([actions.getById, getBestillinger])

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
		bestillingStatuser: any
		redigertePersoner: {
			antallSlettedePersoner: number
		}
	},
	ownProps: FormikProps<any>
) => ({
	...ownProps,
	isFetching: loadingSelector(state),
	selectGruppe: selectGruppeById,
	grupper: state.gruppe,
	visning: state.finnPerson.visning,
	visPerson: state.finnPerson.visPerson,
	sidetall: state.finnPerson.sidetall,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
	bestillingStatuser: state.bestillingStatuser.byId,
	antallSlettet: state.redigertePersoner.antallSlettedePersoner,
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	getGruppe: (gruppeId: number, pageNo: number, pageSize: number) =>
		dispatch(actions.getById(gruppeId, pageNo, pageSize)),
	getBestillinger: (gruppeId: number) => dispatch(getBestillinger(gruppeId)),
	setVisning: (visning: VisningType) => dispatch(setVisning(visning)),
})

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(Gruppe)
