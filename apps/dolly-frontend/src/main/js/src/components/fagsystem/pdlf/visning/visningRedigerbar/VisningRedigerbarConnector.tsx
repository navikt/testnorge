import { connect } from 'react-redux'
import { VisningRedigerbar } from '~/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbar'
import { Dispatch } from 'redux'
import { actions } from '~/ducks/redigertePersoner'

const mapDispatchToProps = (dispatch: Dispatch, ownProps: any) => {
	return {
		getPdlForvalter: () => dispatch(actions.hentPdlforvalterPersoner(ownProps.ident)),
	}
}

export default connect(null, mapDispatchToProps)(VisningRedigerbar)
