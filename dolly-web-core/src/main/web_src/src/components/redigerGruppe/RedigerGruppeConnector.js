import { connect } from 'react-redux'
import { createGruppe, updateGruppe } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import { createErrorMessageSelector } from '~/ducks/errors'
import RedigerGruppe from './RedigerGruppe'

const loadingSelector = createLoadingSelector([createGruppe, updateGruppe])
const errorSelector = createErrorMessageSelector([createGruppe, updateGruppe])

const mapStateToProps = state => ({
	createOrUpdateFetching: loadingSelector(state),
	currentUserId: state.bruker.brukerData.navIdent,
	error: errorSelector(state)
})

const mapDispatchToProps = dispatch => ({
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (id, values) => dispatch(updateGruppe(id, values))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerGruppe)
