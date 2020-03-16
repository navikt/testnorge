import { connect } from 'react-redux'
import Bruker from './Bruker'

const mapStateToProps = (state, ownProps) => ({
	brukerId: state.bruker.brukerData.brukerId
})

export default connect(mapStateToProps)(Bruker)
