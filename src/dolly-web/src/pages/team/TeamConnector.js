import { connect } from 'react-redux'
import { fetchTeams } from '~/ducks/team'
import { getGrupper } from '~/ducks/grupper'
import Team from './Team'

const mapStateToProps = (state, ownProps) => {
	return {
		currentTeamId: ownProps.match.params.teamId,
		team:
			state.team.items &&
			state.team.items.find(t => t.id === parseInt(ownProps.match.params.teamId))
	}
}

const mapDispatchToProps = dispatch => ({
	fetchTeams: () => dispatch(fetchTeams()),
	getGrupper: opts => dispatch(getGrupper(opts))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
