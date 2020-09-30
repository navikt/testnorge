import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import App from './App'
import { getEnvironments } from '~/ducks/environments'
import { clearAllErrors, applicationErrorSelector } from '~/ducks/errors'
import { getCurrentBruker, getCurrentBrukerBilde } from '~/ducks/bruker'
import ConfigService from '~/service/Config'

const mapStateToProps = state => {
	console.log('state :>> ', state)
	return {
		router: state.router,
		brukerData: state.bruker.brukerData,
		brukerBilde: state.bruker.brukerBilde,
		redirectTo: state.common.redirectTo,
		applicationError: applicationErrorSelector(state),
		configReady: ConfigService.verifyConfig()
	}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	onRedirect: url => dispatch(push(url)),
	getCurrentBruker: () => dispatch(getCurrentBruker()),
	getCurrentBrukerBilde: () => dispatch(getCurrentBrukerBilde()),
	clearAllErrors: () => dispatch(clearAllErrors()),
	getEnvironments: () => dispatch(getEnvironments()),
	fetchConfig: () => ConfigService.fetchConfig()
})

export default connect(mapStateToProps, mapDispatchToProps)(App)
