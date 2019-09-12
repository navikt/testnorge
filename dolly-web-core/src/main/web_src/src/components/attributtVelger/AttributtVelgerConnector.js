import { connect } from 'react-redux'
import AttributtVelger from './AttributtVelger'

const mapStateToProps = state => ({
	currentBestilling: state.currentBestilling
})

export default connect(mapStateToProps)(AttributtVelger)
