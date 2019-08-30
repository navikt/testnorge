import { connect } from 'react-redux'
import { actions } from '~/ducks/teams'
import { listGrupper } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import Team from './Team'

const teamLoadingSelector = createLoadingSelector(actions.api.getById)
const grupperLoadingSelector = createLoadingSelector('TODO')
const createRemoveSelector = createLoadingSelector([
	actions.api.addTeamMember,
	actions.api.removeTeamMember
])

const mapStateToProps = (state, ownProps) => {
	const { teamId } = ownProps.match.params

	return {
		team: state.teams.items && state.teams.items[0],
		teamIsFetching: teamLoadingSelector(state),
		visRedigerTeam: state.teams.editTeamId === teamId.toString(),
		grupper: state.gruppe.data,
		grupperIsFetching: grupperLoadingSelector(state),
		isCreateDelete: createRemoveSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const { teamId } = ownProps.match.params
	return {
		getTeam: () => dispatch(actions.api.getById(teamId)),
		deleteTeam: () => dispatch(actions.api.delete(teamId)),
		startRedigerTeam: () => dispatch(actions.ui.startEditTeam(teamId)),
		listGrupper: () => dispatch(listGrupper({ teamId })),
		addMember: userArray => dispatch(actions.api.addTeamMember(teamId, userArray)),
		removeMember: user => dispatch(actions.api.removeTeamMember(teamId, user))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
