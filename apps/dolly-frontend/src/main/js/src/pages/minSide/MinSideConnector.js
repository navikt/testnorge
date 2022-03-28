import { connect } from 'react-redux'
import MinSide from './MinSide'

const mapStateToProps = (state, ownProps) => ({
	brukerId: state.bruker.brukerData.brukerId,
	brukerBilde: ownProps.brukerBilde,
	brukerProfil: ownProps.brukerProfil,
})

export default connect(mapStateToProps)(MinSide)
