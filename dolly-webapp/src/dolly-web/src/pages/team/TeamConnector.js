import { connect } from 'react-redux'
import { getTeam } from '~/ducks/team'
import Team from './Team'

const mapStateToProps = state => {
	return {
		team: state.team.data,
		teamFetching: state.team.fetching,
		grupper: state.grupper.items,
		grupperFetching: state.grupper.fetching
	}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	getTeam: () => dispatch(getTeam(ownProps.match.params.teamId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Team)
