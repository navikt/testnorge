import { connect } from 'react-redux'
import KodeverkValue from './KodeverkValue'
import { fetchKodeverk, kodeverkLabelSelector } from '~/ducks/kodeverk'

const mapStateToProps = (state, ownProps) => {
	const { value, apiKodeverkId } = ownProps
	return {
		kodeverkObject: kodeverkLabelSelector(state, apiKodeverkId, value)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const { value, apiKodeverkId } = ownProps
	return {
		fetchKodeverk: () => dispatch(fetchKodeverk(apiKodeverkId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(KodeverkValue)
