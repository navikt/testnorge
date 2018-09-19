import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import App from './App'
import { fetchCurrentBruker } from '~/ducks/bruker'

const mapStateToProps = state => ({
	router: state.router, // Need to use this to tell mapStateToProps if url changes
	brukerData: state.bruker.brukerData,
	redirectTo: state.common.redirectTo
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	onRedirect: url => dispatch(push(url)),
	fetchCurrentBruker: () => dispatch(fetchCurrentBruker())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(App)
