import { connect } from 'react-redux'
import App from './App'
import { fetchCurrentBruker } from '~/ducks/bruker'

const mapStateToProps = state => ({
	router: state.router, // Need to use this to tell mapStateToProps if url changes
	brukerData: state.bruker.brukerData
})

const mapDispatchToProps = dispatch => ({
	fetchCurrentBruker: () => dispatch(fetchCurrentBruker())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(App)
