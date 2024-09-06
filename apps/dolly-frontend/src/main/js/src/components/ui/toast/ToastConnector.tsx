import { connect } from 'react-redux'
import { applicationErrorSelector, clearAllErrors } from '@/ducks/errors'
import { ErrorToast } from '@/components/ui/toast/ErrorToast'
import { Dispatch } from 'redux'

const mapStateToProps = (state: {}) => ({
	applicationError: applicationErrorSelector(state),
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	clearAllErrors: () => dispatch(clearAllErrors()),
})

export default connect(mapStateToProps, mapDispatchToProps)(ErrorToast)
