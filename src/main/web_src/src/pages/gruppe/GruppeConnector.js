import { connect } from 'react-redux'
import { actions, selectGruppeById } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Gruppe from './Gruppe'

const loadingSelector = createLoadingSelector([actions.getById, getBestillinger])
const loadingSelectorSlettGruppe = createLoadingSelector(actions.remove)
const loadingSelectorLaasGruppe = createLoadingSelector(actions.laas)

const mapStateToProps = (state, ownProps) => ({
	isFetching: loadingSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	isLockingGruppe: loadingSelectorLaasGruppe(state),
	gruppe: selectGruppeById(state, ownProps.match.params.gruppeId),
	identer: state.gruppe.ident,
	brukernavn: state.bruker.brukerData.brukernavn,
	bestillingStatuser: state.bestillingStatuser.byId
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId } = ownProps.match.params
	return {
		getGruppe: () => dispatch(actions.getById(gruppeId)),
		deleteGruppe: () => dispatch(actions.remove(gruppeId)),
		laasGruppe: () =>
			dispatch(actions.laas(gruppeId, { erLaast: true, laastBeskrivelse: 'Låst gruppe' })),
		getBestillinger: () => dispatch(getBestillinger(gruppeId))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Gruppe)
