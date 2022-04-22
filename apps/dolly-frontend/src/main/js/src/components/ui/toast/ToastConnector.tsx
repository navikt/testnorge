import { connect } from 'react-redux'
import { applicationErrorSelector, clearAllErrors } from '~/ducks/errors'
import { Toast } from '~/components/ui/toast/Toast'
import { Dispatch } from 'redux'

const mapStateToProps = (state: {}) => ({
	applicationError: applicationErrorSelector(state),
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	clearAllErrors: () => dispatch(clearAllErrors()),
})

export default connect(mapStateToProps, mapDispatchToProps)(Toast)
