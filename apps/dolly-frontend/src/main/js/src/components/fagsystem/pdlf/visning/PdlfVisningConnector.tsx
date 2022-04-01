import { connect } from 'react-redux'
import { actions } from '~/ducks/redigertePersoner'
import { withRouter } from 'react-router-dom'
import { PdlfVisning } from '~/components/fagsystem'

const mapStateToProps = (state, ownProps) => {
	return {
		tmpPersoner: state?.redigertePersoner?.pdlforvalter,
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getPdlForvalter: () => dispatch(actions.hentPdlforvalterPersoner(ownProps.data?.person?.ident)),
		getPdl: () => dispatch(actions.hentPdlPersoner(ownProps.data?.person?.ident)),
	}
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(PdlfVisning))
