import { connect } from 'react-redux'
import MinSide from './MinSide'

const mapStateToProps = (state, ownProps) => ({
	brukerId: state.bruker.brukerData.brukerId,
	brukerBilde: state.bruker.brukerBilde,
	brukerProfil: state.bruker.brukerProfil,
})

export default connect(mapStateToProps)(MinSide)
