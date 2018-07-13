import { connect } from 'react-redux'
import TeamOversikt from './TeamOversikt'
import {
	fetchTeams,
	setTeamVisning,
	createTeam,
	deleteTeam,
	startOpprettTeam,
	startRedigerTeam,
	closeOpprettRedigerTeam
} from '~/ducks/team'

const mapStateToProps = state => ({
	teams: state.team
})

const mapDispatchToProps = dispatch => ({
	fetchTeams: () => dispatch(fetchTeams()),
	setTeamVisning: visning => dispatch(setTeamVisning(visning)),
	createTeam: data => dispatch(createTeam(data)),
	deleteTeam: teamId => dispatch(deleteTeam(teamId)),
	startOpprettTeam: () => dispatch(startOpprettTeam()),
	startRedigerTeam: teamId => dispatch(startRedigerTeam(teamId)),
	closeOpprettRedigerTeam: () => dispatch(closeOpprettRedigerTeam())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TeamOversikt)
