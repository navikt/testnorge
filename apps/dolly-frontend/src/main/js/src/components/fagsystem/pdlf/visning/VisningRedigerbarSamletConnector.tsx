import { connect } from 'react-redux'
import { Dispatch } from 'redux'
import { hentPdlforvalterPersoner } from '~/ducks/redigertePersoner'
import { VisningRedigerbarSamlet } from '~/components/fagsystem/pdlf/visning/VisningRedigerbarSamlet'

const mapDispatchToProps = (dispatch: Dispatch, ownProps: any) => {
	return {
		getPdlForvalter: () => dispatch(hentPdlforvalterPersoner(ownProps.ident)),
	}
}

export default connect(null, mapDispatchToProps)(VisningRedigerbarSamlet)
