import { connect } from 'react-redux'
import Profil from './Profil'
import { fetchTeams, setTeamVisning, setActivePage } from '~/ducks/team'

const mapStateToProps = state => ({
	teams: state.team
})

const mapDispatchToProps = dispatch => ({
	fetchTeams: () => dispatch(fetchTeams()),
	setTeamVisning: visning => dispatch(setTeamVisning(visning)),
	setActivePage: page => dispatch(setActivePage(page))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Profil)
