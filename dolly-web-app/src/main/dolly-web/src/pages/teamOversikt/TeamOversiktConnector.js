import { connect } from 'react-redux'
import TeamOversikt from './TeamOversikt'
import { fetchTeamsForUser, actions, sokSelector } from '~/ducks/teams'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector([actions.api.get, actions.api.getByUserId])

const mapStateToProps = state => ({
	isFetching: loadingSelector(state),
	searchActive: Boolean(state.search),
	teamListe: sokSelector(state.teams.items, state.search),
	teams: state.teams
})

const mapDispatchToProps = dispatch => ({
	fetchTeamsForUser: () => dispatch(fetchTeamsForUser()),
	fetchAllTeams: () => dispatch(actions.api.get()),
	createTeam: data => dispatch(actions.api.create(data))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TeamOversikt)
