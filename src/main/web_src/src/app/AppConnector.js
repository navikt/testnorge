import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import App from './App'
import { getEnvironments } from '~/ducks/environments'
import { clearAllErrors, applicationErrorSelector } from '~/ducks/errors'
import { getCurrentBruker } from '~/ducks/bruker'
import { getVarslinger, getVarslingerBruker, updateVarslingerBruker } from '~/ducks/varslinger'
import { createLoadingSelector } from '~/ducks/loading'
import ConfigService from '~/service/Config'

const loading = createLoadingSelector(getVarslingerBruker)

const mapStateToProps = state => {
	const varslinger =
		loading(state) === false && state.varslinger.varslingerData
			? state.varslinger.varslingerData.filter(
					varsel => !state.varslinger.varslingerBrukerData.includes(varsel.varslingId)
			  )
			: []

	return {
		router: state.router,
		brukerData: state.bruker.brukerData,
		varslinger: varslinger,
		redirectTo: state.common.redirectTo,
		applicationError: applicationErrorSelector(state),
		configReady: ConfigService.verifyConfig()
	}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	onRedirect: url => dispatch(push(url)),
	getCurrentBruker: () => dispatch(getCurrentBruker()),
	getVarslingerBruker: () => dispatch(getVarslingerBruker()),
	getVarslinger: () => dispatch(getVarslinger()),
	updateVarslingerBruker: varslingId => dispatch(updateVarslingerBruker(varslingId)),
	clearAllErrors: () => dispatch(clearAllErrors()),
	getEnvironments: () => dispatch(getEnvironments()),
	fetchConfig: () => ConfigService.fetchConfig()
})

export default connect(mapStateToProps, mapDispatchToProps)(App)
