import { connect } from 'react-redux'
import { actions } from '~/ducks/team'
import { getGrupper } from '~/ducks/grupper'
import { createLoadingSelector } from '~/ducks/loading'
import Team from './Team'

const teamLoadingSelector = createLoadingSelector(actions.team.get)
const grupperLoadingSelector = createLoadingSelector('TODO')

const mapStateToProps = state => {
	return {
		team: state.team.data,
		teamIsFetching: teamLoadingSelector(state),
		grupper: state.grupper.items,
		grupperIsFetching: grupperLoadingSelector(state)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const { teamId } = ownProps.match.params
	return {
		getTeam: () => dispatch(actions.team.get(teamId)),
		getGrupper: () => dispatch(getGrupper(teamId)),
		addMember: userArray => dispatch(actions.team.addMember(teamId, userArray)),
		removeMember: userArray => dispatch(actions.team.removeMember(teamId, userArray))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
