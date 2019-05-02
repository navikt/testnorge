import { connect } from 'react-redux'
import AutofillAddress from './AutofillAddress'

const mapStateToProps = state => ({
	values: state.currentBestilling.values
})

export default connect(mapStateToProps)(AutofillAddress)
