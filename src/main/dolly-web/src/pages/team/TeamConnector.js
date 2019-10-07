import { connect } from 'react-redux'
import { actions } from '~/ducks/teams'
import { listGrupper, getGrupperByTeamId } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import Team from './Team'

const teamLoadingSelector = createLoadingSelector(actions.api.getById)
const loadingSelectorSletteTeam = createLoadingSelector(actions.api.delete)
const grupperLoadingSelector = createLoadingSelector(getGrupperByTeamId)

const mapStateToProps = (state, ownProps) => ({
	team: state.teams.items && state.teams.items[0],
	teamIsFetching: teamLoadingSelector(state),
	grupper: state.gruppe.data,
	grupperIsFetching: grupperLoadingSelector(state),
	isDeletingTeam: loadingSelectorSletteTeam(state)
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { teamId } = ownProps.match.params
	return {
		getTeam: () => dispatch(actions.api.getById(teamId)),
		deleteTeam: () => dispatch(actions.api.delete(teamId)),
		listGrupper: () => dispatch(listGrupper({ teamId })),
		addMember: userArray => dispatch(actions.api.addTeamMember(teamId, userArray)),
		removeMember: user => dispatch(actions.api.removeTeamMember(teamId, user))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
