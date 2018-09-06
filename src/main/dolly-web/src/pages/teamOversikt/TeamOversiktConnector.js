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
} from '~/ducks/teams'

const teamFiltering = (items, searchText) => {
	if (!items) return null

	if (!searchText) return items

	const query = searchText.toLowerCase()
	return items.filter(item => item.navn.toLowerCase().includes(query))
}

const mapStateToProps = state => ({
	teamListe: teamFiltering(state.teams.items, state.search),
	teams: state.teams
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
