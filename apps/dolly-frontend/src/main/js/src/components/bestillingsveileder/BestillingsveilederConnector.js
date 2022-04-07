import { connect } from 'react-redux'
import { sendBestilling } from '~/ducks/bestilling'
import { Bestillingsveileder } from './Bestillingsveileder'

const mapStateToProps = (state) => ({
	brukertype: state.bruker.brukerData.brukertype,
	brukerId: state.bruker.brukerData.brukerId,
	error: state.bestveil.error,
})

const mapDispatchToProps = (dispatch) => ({
	sendBestilling: (values, opts, gruppeId) => dispatch(sendBestilling(values, opts, gruppeId)),
})

export default connect(mapStateToProps, mapDispatchToProps)(Bestillingsveileder)
