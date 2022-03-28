import { connect } from 'react-redux'
import { getEnvironments } from '~/ducks/environments'
import { applicationErrorSelector, clearAllErrors } from '~/ducks/errors'
import { getCurrentBruker } from '~/ducks/bruker'
import { updateVarslingerBruker } from '~/ducks/varslinger'
import { App } from '~/app/App'

const mapStateToProps = (state) => ({
	router: state.router,
	brukerData: state.bruker.brukerData,
	redirectTo: state.common.redirectTo,
	applicationError: applicationErrorSelector(state),
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getCurrentBruker: () => dispatch(getCurrentBruker()),
	updateVarslingerBruker: (varslingId) => dispatch(updateVarslingerBruker(varslingId)),
	clearAllErrors: () => dispatch(clearAllErrors()),
	getEnvironments: () => dispatch(getEnvironments()),
})

export default connect(mapStateToProps, mapDispatchToProps)(App)
