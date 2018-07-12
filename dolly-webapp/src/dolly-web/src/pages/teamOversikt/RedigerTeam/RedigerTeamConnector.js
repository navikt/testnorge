import { connect } from 'react-redux'
import RedigerTeam from './RedigerTeam'
import { createTeam, updateTeam, closeOpprettRedigerTeam } from '~/ducks/team'

const mapStateToProps = state => ({})

const mapDispatchToProps = dispatch => ({
	createTeam: () => dispatch(createTeam()),
	updateTeam: (teamId, values) => dispatch(updateTeam(teamId, values)),
	closeOpprettRedigerTeam: () => dispatch(closeOpprettRedigerTeam())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTeam)
