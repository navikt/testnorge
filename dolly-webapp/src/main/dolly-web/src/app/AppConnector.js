import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import App from './App'
import { clearAllErrors, applicationErrorSelector } from '~/ducks/errors'
import { getCurrentBruker } from '~/ducks/bruker'
import ConfigService from '~/service/Config'

const mapStateToProps = state => ({
	router: state.router, // Need to use this to tell mapStateToProps if url changes
	brukerData: state.bruker.brukerData,
	redirectTo: state.common.redirectTo,
	applicationError: applicationErrorSelector(state),
	configReady: ConfigService.verifyConfig()
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	onRedirect: url => dispatch(push(url)),
	getCurrentBruker: () => dispatch(getCurrentBruker()),
	clearAllErrors: () => dispatch(clearAllErrors()),
	fetchConfig: () => ConfigService.fetchConfig()
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(App)
