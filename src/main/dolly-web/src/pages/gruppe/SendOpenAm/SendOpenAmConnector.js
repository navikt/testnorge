import { connect } from 'react-redux'
import SendOpenAm from './SendOpenAm'
import { postOpenAm, sendToOpenAm } from '~/ducks/openam'
import { createLoadingSelector } from '~/ducks/loading'

const openAmSelector = createLoadingSelector(postOpenAm)
const mapStateToProps = state => {
	return {
		openAmFetching: openAmSelector(state)
	}
}

const mapDispatchToProps = dispatch => {
	return {
		sendToOpenAm: bestillingId => dispatch(sendToOpenAm(bestillingId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SendOpenAm)
