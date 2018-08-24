import { connect } from 'react-redux'
import { getTeam, addMember, removeMember } from '~/ducks/team'
import Team from './Team'

const mapStateToProps = state => {
	return {
		team: state.team.data,
		teamFetching: state.team.fetching,
		grupper: state.grupper.items,
		grupperFetching: state.grupper.fetching
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const teamId = ownProps.match.params.teamId
	return {
		getTeam: () => dispatch(getTeam(teamId)),
		addMember: userArray => dispatch(addMember(teamId, userArray)),
		removeMember: userArray => dispatch(removeMember(teamId, userArray))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
