import { connect } from 'react-redux'
import { VisningRedigerbar } from '~/components/fagsystem/pdlf/visning/VisningRedigerbar'
import { Dispatch } from 'redux'
import { hentPdlforvalterPersoner } from '~/ducks/redigertePersoner'

const mapDispatchToProps = (dispatch: Dispatch, ownProps: any) => {
	return {
		getPdlForvalter: () => dispatch(hentPdlforvalterPersoner(ownProps.ident)),
	}
}

export default connect(null, mapDispatchToProps)(VisningRedigerbar)
