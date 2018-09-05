import { connect } from 'react-redux'
import Splashscreen from './Splashscreen'
import { setSplashscreenAccepted } from '~/ducks/bruker'

const mapStateToProps = state => ({
	brukerData: state.bruker.brukerData
})

const mapDispatchToProps = dispatch => ({
	setSplashscreenAccepted: () => dispatch(setSplashscreenAccepted())
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Splashscreen)
