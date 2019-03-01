import { connect } from 'react-redux'
import SendOpenAm from './SendOpenAm'
import { postOpenAm, sendToOpenAm } from '~/ducks/openam'
import { createLoadingSelector } from '~/ducks/loading'

const openAmSelector = createLoadingSelector(postOpenAm)
const mapStateToProps = state => {
	// console.log('state :', state)
	return {
		openAmFetching: openAmSelector(state),
		openAmResponse: state.openam.response
	}
}

const mapDispatchToProps = dispatch => {
	// console.log('dispatch :', dispatch)
	return {
		sendToOpenAm: bestillingId => dispatch(sendToOpenAm(bestillingId))
		// hideButton: () => dispatch(hideButton)
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SendOpenAm)
