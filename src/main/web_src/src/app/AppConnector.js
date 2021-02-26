import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import App from './App'
import { getEnvironments } from '~/ducks/environments'
import { applicationErrorSelector, clearAllErrors } from '~/ducks/errors'
import { getCurrentBruker, getCurrentBrukerBilde, getCurrentBrukerProfil } from '~/ducks/bruker'
import { getVarslinger, getVarslingerBruker, updateVarslingerBruker } from '~/ducks/varslinger'
import { createLoadingSelector } from '~/ducks/loading'
import ConfigService from '~/service/Config'

const loadingVarslinger = createLoadingSelector(getVarslingerBruker)

const mapStateToProps = state => ({
	router: state.router,
	brukerData: state.bruker.brukerData,
	brukerProfil: state.bruker.brukerProfil,
	brukerBilde: state.bruker.brukerBilde,
	varslinger: state.varslinger.varslingerData,
	varslingerBruker: state.varslinger.varslingerBrukerData,
	isLoadingVarslinger: loadingVarslinger(state),
	redirectTo: state.common.redirectTo,
	applicationError: applicationErrorSelector(state),
	configReady: ConfigService.verifyConfig()
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	onRedirect: url => dispatch(push(url)),
	getCurrentBruker: () => dispatch(getCurrentBruker()),
	getCurrentBrukerProfil: () => dispatch(getCurrentBrukerProfil()),
	getCurrentBrukerBilde: () => dispatch(getCurrentBrukerBilde()),
	getVarslingerBruker: () => dispatch(getVarslingerBruker()),
	getVarslinger: () => dispatch(getVarslinger()),
	updateVarslingerBruker: varslingId => dispatch(updateVarslingerBruker(varslingId)),
	clearAllErrors: () => dispatch(clearAllErrors()),
	getEnvironments: () => dispatch(getEnvironments()),
	fetchConfig: () => ConfigService.fetchConfig()
})

export default connect(mapStateToProps, mapDispatchToProps)(App)
