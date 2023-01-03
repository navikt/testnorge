import { connect } from 'react-redux'
import { Dispatch } from 'redux'
import { actions } from '@/ducks/redigertePersoner'
import { VisningRedigerbarSamlet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarSamlet'

const mapDispatchToProps = (dispatch: Dispatch, ownProps: any) => {
	return {
		getPdlForvalter: () => dispatch(actions.hentPdlforvalterPersoner(ownProps.ident)),
	}
}

export default connect(null, mapDispatchToProps)(VisningRedigerbarSamlet)
