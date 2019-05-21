import { connect } from 'react-redux'
import AutofillAddress from './AutofillAddress'
import { actions } from '~/ducks/bestilling'

const mapStateToProps = state => ({
	currentMal: state.currentBestilling.currentMal,
	values: state.currentBestilling.values
})

const mapDispatchToProps = dispatch => ({
	setValues: values => dispatch(actions.setValues(values))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(AutofillAddress)
