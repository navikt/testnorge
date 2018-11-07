import { connect } from 'react-redux'
import SendOpenAm from './SendOpenAm'
import { postOpenAm, sendToOpenAm } from '~/ducks/openam'
import { createLoadingSelector } from '~/ducks/loading'

const openAmSelector = createLoadingSelector(postOpenAm)

const mapStateToProps = state => {
	return {
		openAmFetching: openAmSelector(state),
		openAmResponse: state.openam.response
	}
}

const mapDispatchToProps = dispatch => {
	return {
		sendToOpenAm: () => dispatch(sendToOpenAm())
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SendOpenAm)
