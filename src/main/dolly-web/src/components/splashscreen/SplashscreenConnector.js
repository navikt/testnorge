import { connect } from 'react-redux'
import Splashscreen from './Splashscreen'
import { setSplashscreenAccepted } from '~/ducks/bruker'

const mapDispatchToProps = dispatch => ({
	setSplashscreenAccepted: () => dispatch(setSplashscreenAccepted())
})

export default connect(
	null,
	mapDispatchToProps
)(Splashscreen)
