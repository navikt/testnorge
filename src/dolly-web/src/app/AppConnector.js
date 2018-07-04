import { connect } from 'react-redux'
import App from './App'
import { fetchCurrentBruker } from '~/ducks/bruker'

const mapStateToProps = state => ({
	brukerData: state.bruker.brukerData
})

const mapDispatchToProps = dispatch => ({
	fetchCurrentBruker: () => dispatch(fetchCurrentBruker())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(App)
