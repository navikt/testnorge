import { connect } from 'react-redux'
import Gruppe from './Gruppe'
import { getGruppe, showCreateOrEditGroup } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(getGruppe)
const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	gruppeArray: state.gruppe.data,
	createOrUpdateId: state.gruppe.createOrUpdateId
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getGruppe: () => dispatch(getGruppe(ownProps.match.params.gruppeId)),
	createGroup: () => dispatch(showCreateOrEditGroup(-1))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Gruppe)
