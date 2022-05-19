import { actions } from '~/ducks/redigertePersoner'
import { connect } from 'react-redux'
import { Dispatch } from 'redux'
import { VisningRedigerbarPersondetaljer } from '~/components/fagsystem/pdlf/visning/VisningRedigerbarPersondetaljer'

const mapDispatchToProps = (dispatch: Dispatch, ownProps: any) => {
	return {
		getPdlForvalter: () => dispatch(actions.hentPdlforvalterPersoner(ownProps.ident)),
	}
}

export default connect(null, mapDispatchToProps)(VisningRedigerbarPersondetaljer)
