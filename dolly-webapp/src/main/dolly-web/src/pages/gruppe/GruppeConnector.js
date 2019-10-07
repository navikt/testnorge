import { connect } from 'react-redux'
import { getGruppe, deleteGruppe, antallBestillingerSelector } from '~/ducks/gruppe'
import { getBestillinger } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import { resetSearch } from '~/ducks/search'
import Gruppe from './Gruppe'

const loadingSelector = createLoadingSelector(getGruppe)
const loadingSelectorSlettGruppe = createLoadingSelector(deleteGruppe)

const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	isDeletingGruppe: loadingSelectorSlettGruppe(state),
	gruppeArray: state.gruppe.data,
	antallBestillinger: antallBestillingerSelector(state.gruppe.data)
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId } = ownProps.match.params
	return {
		getGruppe: () => dispatch(getGruppe(gruppeId)),
		deleteGruppe: () => dispatch(deleteGruppe(gruppeId)),
		getBestillinger: () => dispatch(getBestillinger(gruppeId)),
		resetSearch: () => dispatch(resetSearch())
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
