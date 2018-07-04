import { connect } from 'react-redux'
import Profil from './Profil'
import { fetchTeams } from '~/ducks/team'

const mapStateToProps = (state, ownProps) => ({
	teams: state.team
})

const mapDispatchToProps = dispatch => ({
	fetchTeams: () => dispatch(fetchTeams())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Profil)
