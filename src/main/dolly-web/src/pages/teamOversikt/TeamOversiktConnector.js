import { connect } from 'react-redux'
import TeamOversikt from './TeamOversikt'
import { fetchTeams, actions } from '~/ducks/teams'

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
	setTeamVisning: visning => dispatch(actions.ui.setTeamVisning(visning)),
	createTeam: data => dispatch(actions.api.create(data)),
	deleteTeam: teamId => dispatch(actions.api.delete(teamId)),
	startOpprettTeam: () => dispatch(actions.ui.startCreateTeam()),
	startRedigerTeam: teamId => dispatch(actions.ui.startEditTeam(teamId)),
	closeOpprettRedigerTeam: () => dispatch(actions.ui.closeCreateEditTeam())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TeamOversikt)
