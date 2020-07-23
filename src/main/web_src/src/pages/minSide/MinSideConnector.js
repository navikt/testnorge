import { connect } from 'react-redux'
import MinSide from './MinSide'

const mapStateToProps = (state, ownProps) => ({
	brukerId: state.bruker.brukerData.brukerId
})

export default connect(mapStateToProps)(MinSide)
