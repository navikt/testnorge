import { connect } from 'react-redux'
import { actions } from '~/ducks/team'
import { listGrupper } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import Team from './Team'

const teamLoadingSelector = createLoadingSelector(actions.team.get)
const grupperLoadingSelector = createLoadingSelector('TODO')

const mapStateToProps = state => {
	return {
		team: state.team.data,
		teamIsFetching: teamLoadingSelector(state),
		grupper: state.gruppe.data,
		grupperIsFetching: grupperLoadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const { teamId } = ownProps.match.params
	return {
		getTeam: () => dispatch(actions.team.get(teamId)),
		listGrupper: () => dispatch(listGrupper(teamId)),
		addMember: userArray => dispatch(actions.team.addMember(teamId, userArray)),
		removeMember: userArray => dispatch(actions.team.removeMember(teamId, userArray))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
