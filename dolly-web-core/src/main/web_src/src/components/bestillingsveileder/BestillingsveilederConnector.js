import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { actions, sendBestilling } from '~/ducks/bestilling'
import { Bestillingsveileder } from './Bestillingsveileder'

const mapStateToProps = state => {
	return {
		maler: state.currentBestilling.maler,
		kodeverk: state.kodeverk.data
	}
}

const mapDispatchToProps = (dispatch, ownProps) => ({
	...bindActionCreators(actions, dispatch),
	sendBestilling: values => dispatch(sendBestilling(values, ownProps.match.params.gruppeId))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(Bestillingsveileder)
