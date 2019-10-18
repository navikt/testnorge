import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import {
	updateTestbruker,
	GET_TPSF_TESTBRUKERE,
	GET_SIGRUN_TESTBRUKER,
	GET_KRR_TESTBRUKER
} from '~/ducks/testBruker'
import { getGruppe } from '~/ducks/gruppe'
import RedigerTestbruker from './RedigerTestbruker'

const mapStateToProps = (state, ownProps) => ({
	testbruker: state.testbruker.items,
	ident: ownProps.match.params.ident,
	bestillinger: state.bestillingStatuser
})

const mapDispatchToProps = (dispatch, ownProps) => {
	const { gruppeId, ident } = ownProps.match.params
	return {
		getTestbruker: () => dispatch(GET_TPSF_TESTBRUKERE([ident])),
		getSigrunTestbruker: () => dispatch(GET_SIGRUN_TESTBRUKER(ident)),
		getKrrTestbruker: () => dispatch(GET_KRR_TESTBRUKER(ident)),
		getTestbrukerPersonoppslag: () => dispatch(GET_TESTBRUKER_PERSONOPPSLAG(ident)),
		getGruppe: () => dispatch(getGruppe(gruppeId)),
		updateTestbruker: (newValues, attributtListe) =>
			dispatch(updateTestbruker(newValues, attributtListe, ident)),
		goBack: () => dispatch(push(`/gruppe/${gruppeId}`))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTestbruker)
