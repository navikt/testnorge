import { connect } from 'react-redux'
import { createGruppe, createTeam, updateGruppe } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import { createErrorMessageSelector } from '~/ducks/errors'
import RedigerGruppe from './RedigerGruppe'

const loadingSelector = createLoadingSelector([createGruppe, createTeam, updateGruppe])
const errorSelector = createErrorMessageSelector([createGruppe, createTeam, updateGruppe])

const mapStateToProps = state => ({
	createOrUpdateFetching: loadingSelector(state),
	currentUserId: state.bruker.brukerData.navIdent,
	error: errorSelector(state),
	teamId: state.gruppe.teamId
})

const mapDispatchToProps = dispatch => ({
	createTeam: nyttTeam => dispatch(createTeam(nyttTeam)),
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (id, values) => dispatch(updateGruppe(id, values))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerGruppe)
