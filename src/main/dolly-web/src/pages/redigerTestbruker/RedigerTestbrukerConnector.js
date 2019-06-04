import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import {
	updateTestbruker,
	GET_TPSF_TESTBRUKERE,
	GET_SIGRUN_TESTBRUKER,
	GET_KRR_TESTBRUKER,
	findEnvironmentsForIdent
} from '~/ducks/testBruker'
import { getGruppe } from '~/ducks/gruppe'
import RedigerTestbruker from './RedigerTestbruker'
import { GET_PERSONOPPSLAG_TESTBRUKER } from '../../ducks/testBruker'

const mapStateToProps = (state, ownProps) => {
	return {
		testbruker: state.testbruker.items,
		ident: ownProps.match.params.ident,
		bestillinger: state.bestillingStatuser
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getTestbruker: () => dispatch(GET_TPSF_TESTBRUKERE([ownProps.match.params.ident])),
		getSigrunTestbruker: () => dispatch(GET_SIGRUN_TESTBRUKER(ownProps.match.params.ident)),
		getKrrTestbruker: () => dispatch(GET_KRR_TESTBRUKER(ownProps.match.params.ident)),
		getPersonoppslagTestbruker: () =>
			dispatch(GET_PERSONOPPSLAG_TESTBRUKER(ownProps.match.params.ident)),
		getGruppe: () => dispatch(getGruppe(ownProps.match.params.gruppeId)),
		updateTestbruker: (newValues, attributtListe) =>
			dispatch(updateTestbruker(newValues, attributtListe, ownProps.match.params.ident)),
		goBack: () => dispatch(push(`/gruppe/${ownProps.match.params.gruppeId}`))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(RedigerTestbruker)
