import { connect } from 'react-redux'
import { sendBestilling } from '@/ducks/bestilling'
import { Bestillingsveileder } from './Bestillingsveileder'

const mapStateToProps = (state) => ({
	error: state.bestveil.error,
})

const mapDispatchToProps = (dispatch) => ({
	sendBestilling: (values, opts, gruppeId, navigate) =>
		dispatch(sendBestilling(values, opts, gruppeId, navigate)),
})

export default connect(mapStateToProps, mapDispatchToProps)(Bestillingsveileder)
