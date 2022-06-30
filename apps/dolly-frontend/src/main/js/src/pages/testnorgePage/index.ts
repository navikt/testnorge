import TestnorgePage from './TestnorgePage'
import { connect } from 'react-redux'

const mapStateToProps = (state: any) => ({
	gruppe: state?.router?.location?.state?.gruppe,
})

export default connect(mapStateToProps)(TestnorgePage)
