import { connect } from 'react-redux'
import { sendBestilling } from '~/ducks/bestilling'
import { Bestillingsveileder } from './Bestillingsveileder'

const mapStateToProps = state => {
	return {}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	sendBestilling: values => dispatch(sendBestilling(values, ownProps.match.params.gruppeId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Bestillingsveileder)
