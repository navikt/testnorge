import { connect } from 'react-redux'
import KodeverkValue from './KodeverkValue'
import { fetchKodeverk, oppslagLabelSelector } from '~/ducks/oppslag'

const mapStateToProps = (state, ownProps) => {
	const { value, apiKodeverkId } = ownProps
	return {
		kodeverkObject: oppslagLabelSelector(state, apiKodeverkId, value)
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
