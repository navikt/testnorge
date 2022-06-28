import TestnorgePage from './TestnorgePage'
import { connect } from 'react-redux'

const mapStateToProps = (state: any) => ({
	gruppeId: state?.router?.location?.state?.gruppeId,
})

export default connect(mapStateToProps)(TestnorgePage)
