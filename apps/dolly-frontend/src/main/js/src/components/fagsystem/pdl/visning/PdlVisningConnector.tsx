import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'

const mapStateToProps = (state, ownProps) => {
	return {
		tmpPersoner: state?.redigertePersoner?.pdl,
	}
}

export default withRouter(connect(mapStateToProps)(PdlVisning))
