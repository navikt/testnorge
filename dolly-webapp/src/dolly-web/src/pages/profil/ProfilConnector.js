import { connect } from 'react-redux'
import Profil from './Profil'
import { fetchTeams, setTeamVisning, setActivePage, createTeam } from '~/ducks/team'

const mapStateToProps = state => ({
	teams: state.team
})

const mapDispatchToProps = dispatch => ({
	fetchTeams: () => dispatch(fetchTeams()),
	setTeamVisning: visning => dispatch(setTeamVisning(visning)),
	createTeam: data => dispatch(createTeam(data))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Profil)
