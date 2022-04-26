import { actions } from '~/ducks/redigertePersoner'
import { connect } from 'react-redux'
import { VisningRedigerbar } from '~/components/fagsystem/pdlf/visning/VisningRedigerbar'
import { Dispatch } from 'redux'

const mapDispatchToProps = (dispatch: Dispatch, ownProps: any) => {
	return {
		getPdlForvalter: () => dispatch(actions.hentPdlforvalterPersoner(ownProps.ident)),
	}
}

export default connect(null, mapDispatchToProps)(VisningRedigerbar)
