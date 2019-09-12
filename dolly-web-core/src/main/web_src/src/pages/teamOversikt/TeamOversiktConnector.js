import { connect } from 'react-redux'
import TeamOversikt from './TeamOversikt'
import { fetchTeams, actions, sokSelector } from '~/ducks/teams'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector([
	actions.api.get,
	actions.api.getById,
	actions.api.getByUserId
])

const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	searchActive: Boolean(state.search),
	teamListe: sokSelector(state.teams.items, state.search),
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
