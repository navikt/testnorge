import { connect } from 'react-redux'
import RedigerGruppe from './RedigerGruppe'
import { closeCreateOrEdit, createGruppe, createTeam, toggleCreateTeam, updateGruppe } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import { createErrorMessageSelector } from '~/ducks/errors'

const loadingSelector = createLoadingSelector([createGruppe, createTeam, updateGruppe])
const errorSelector = createErrorMessageSelector([createGruppe, createTeam, updateGruppe])

const mapStateToProps = state => ({
	createOrUpdateFetching: loadingSelector(state),
	currentUserId: state.bruker.brukerData.navIdent,
	error: errorSelector(state),
	team: state.gruppe.team
})

const mapDispatchToProps = dispatch => ({
	createTeam: nyttTeam => dispatch(createTeam(nyttTeam)),
	createGruppe: nyGruppe => dispatch(createGruppe(nyGruppe)),
	updateGruppe: (id, values) => dispatch(updateGruppe(id, values)),
	onCancel: () => dispatch(closeCreateOrEdit()),
	toggleCreateTeam: (value) => dispatch(toggleCreateTeam(value))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerGruppe)