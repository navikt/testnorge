import { connect } from 'react-redux'
import RedigerTeam from './RedigerTeam'
import { actions } from '~/ducks/teams'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelector = createLoadingSelector(actions.api.update)

const mapStateToProps = state => ({
	teamIsUpdating: loadingSelector(state)
})

const mapDispatchToProps = dispatch => ({
	createTeam: values => dispatch(actions.api.create(values)),
	updateTeam: (teamId, values) => dispatch(actions.api.update(teamId, values)),
	closeOpprettRedigerTeam: () => dispatch(actions.ui.closeCreateEditTeam())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTeam)
