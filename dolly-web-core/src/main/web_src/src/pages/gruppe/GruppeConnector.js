import { connect } from 'react-redux'
import { actions, selectGruppeById } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Gruppe from './Gruppe'

const loadingSelector = createLoadingSelector([actions.getById, getBestillinger])
const loadingSelectorSlettGruppe = createLoadingSelector(actions.remove)

const mapStateToProps = (state, ownProps) => ({
	isFetching: loadingSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	gruppe: selectGruppeById(state, ownProps.match.params.gruppeId),
	identer: state.gruppe.ident
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId } = ownProps.match.params
	return {
		getGruppe: () => dispatch(actions.getById(gruppeId)),
		deleteGruppe: () => dispatch(actions.remove(gruppeId)),
		getBestillinger: () => dispatch(getBestillinger(gruppeId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
