import { connect } from 'react-redux'
import { actions } from '~/ducks/teams'
import { listGrupper } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import Team from './Team'

const teamLoadingSelector = createLoadingSelector(actions.api.getById)
const grupperLoadingSelector = createLoadingSelector('TODO')

const mapStateToProps = (state, ownProps) => {
	return {
		team: state.teams.items,
		teamIsFetching: teamLoadingSelector(state),
		grupper: state.gruppe.data,
		grupperIsFetching: grupperLoadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const { teamId } = ownProps.match.params
	return {
		getTeam: () => dispatch(actions.api.getById(teamId)),
		listGrupper: () => dispatch(listGrupper({ teamId })),
		addMember: userArray => dispatch(actions.api.addTeamMember(teamId, userArray)),
		removeMember: userArray => dispatch(actions.api.removeTeamMember(teamId, userArray))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
