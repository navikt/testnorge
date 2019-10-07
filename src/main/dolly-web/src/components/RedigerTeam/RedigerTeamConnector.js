import { connect } from 'react-redux'
import { actions } from '~/ducks/teams'
import { createLoadingSelector } from '~/ducks/loading'
import RedigerTeam from './RedigerTeam'

const loadingSelector = createLoadingSelector(actions.api.update)

const mapStateToProps = state => ({
	teamIsUpdating: loadingSelector(state)
})

const mapDispatchToProps = dispatch => ({
	createTeam: values => dispatch(actions.api.create(values)),
	updateTeam: (teamId, values) => dispatch(actions.api.update(teamId, values))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTeam)
