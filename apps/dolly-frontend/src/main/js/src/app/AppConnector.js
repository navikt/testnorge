import { connect } from 'react-redux'
import { getEnvironments } from '~/ducks/environments'
import { clearAllErrors } from '~/ducks/errors'
import { getCurrentBruker } from '~/ducks/bruker'
import { updateVarslingerBruker } from '~/ducks/varslinger'
import { App } from '~/app/App'

const mapStateToProps = (state) => ({
	brukerData: state.bruker.brukerData,
	// applicationError: applicationErrorSelector(state),
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getCurrentBruker: () => dispatch(getCurrentBruker()),
	updateVarslingerBruker: (varslingId) => dispatch(updateVarslingerBruker(varslingId)),
	clearAllErrors: () => dispatch(clearAllErrors()),
	getEnvironments: () => dispatch(getEnvironments()),
})

export default connect(mapStateToProps, mapDispatchToProps)(App)
