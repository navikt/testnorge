import { connect } from 'react-redux'
import Profil from './Profil'
import { fetchTeams, actions } from '~/ducks/teams'

const mapStateToProps = state => ({
	bruker: state.bruker.brukerData
})

const mapDispatchToProps = dispatch => ({
	fetchTeams: () => dispatch(fetchTeams()),
	setTeamVisning: visning => dispatch(actions.ui.setTeamVisning(visning)),
	createTeam: data => dispatch(actions.api.create(data))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Profil)
