import { connect } from 'react-redux'
import { sendBestilling } from '~/ducks/bestilling'
import { Bestillingsveileder } from './Bestillingsveileder'

const mapStateToProps = (state) => ({
	brukertype: state.bruker.brukerData.brukertype,
	error: state.bestveil.error,
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	sendBestilling: (values, opts) =>
		dispatch(sendBestilling(values, opts, ownProps.match.params.gruppeId)),
})

export default connect(mapStateToProps, mapDispatchToProps)(Bestillingsveileder)
