import { actions } from '~/ducks/redigertePersoner'
import { connect } from 'react-redux'
import { VisningRedigerbar } from '~/components/fagsystem/pdlf/visning/VisningRedigerbar'

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getPdlForvalter: () => dispatch(actions.hentPdlforvalterPersoner(ownProps.ident)),
	}
}

export default connect(null, mapDispatchToProps)(VisningRedigerbar)
