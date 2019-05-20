import { connect } from 'react-redux'
import AutofillAddress from './AutofillAddress'

const mapStateToProps = state => ({
	currentMal: state.currentBestilling.currentMal,
	values: state.currentBestilling.values
})

export default connect(mapStateToProps)(AutofillAddress)
