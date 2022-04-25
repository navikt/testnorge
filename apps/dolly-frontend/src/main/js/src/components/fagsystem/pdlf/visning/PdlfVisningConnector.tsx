import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { PdlfVisning } from '~/components/fagsystem'

const mapStateToProps = (state, ownProps) => {
	return {
		tmpPersoner: state?.redigertePersoner?.pdlforvalter,
	}
}

export default withRouter(connect(mapStateToProps)(PdlfVisning))
