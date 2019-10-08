import { connect } from 'react-redux'
import { actions } from '~/ducks/teams'
import { getGrupperByTeamId } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import Team from './Team'

const loadingSelector = createLoadingSelector([actions.api.getById, getGrupperByTeamId])
const loadingSelectorSletteTeam = createLoadingSelector(actions.api.delete)

const mapStateToProps = (state, ownProps) => ({
	isFetching: loadingSelector(state),
	team: state.teams.items && state.teams.items[0],
	grupper: state.gruppe.data,
	isDeletingTeam: loadingSelectorSletteTeam(state)
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { teamId } = ownProps.match.params
	return {
		getTeam: () => dispatch(actions.api.getById(teamId)),
		deleteTeam: () => dispatch(actions.api.delete(teamId)),
		getGrupperByTeamId: () => dispatch(getGrupperByTeamId(teamId)),
		addMember: userArray => dispatch(actions.api.addTeamMember(teamId, userArray)),
		removeMember: user => dispatch(actions.api.removeTeamMember(teamId, user))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
