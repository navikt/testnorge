import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { createSelector } from 'reselect'
import { push } from 'connected-react-router'
import { getBestillingById } from '~/ducks/bestillingStatus'
import { getIdentByIdSelector } from '~/ducks/gruppe'
import {
	getDataFraFagsystemer,
	getDataForIdent,
	GET_KRR_TESTBRUKER,
	GET_SIGRUN_TESTBRUKER,
	GET_SIGRUN_SEKVENSNR,
	GET_AAREG_TESTBRUKER,
	GET_TESTBRUKER_PERSONOPPSLAG,
	GET_ARENA_TESTBRUKER,
	GET_INST_TESTBRUKER,
	GET_UDI_TESTBRUKER
} from '~/ducks/testBruker'
import { FRIGJOER_TESTBRUKER } from '~/ducks/testBruker'
import { createLoadingSelector } from '~/ducks/loading'
import { PersonVisning } from './PersonVisning'

const loadingSelectorKrr = createLoadingSelector(GET_KRR_TESTBRUKER)
const loadingSelectorSigrun = createLoadingSelector([GET_SIGRUN_TESTBRUKER, GET_SIGRUN_SEKVENSNR])
const loadingSelectorAareg = createLoadingSelector(GET_AAREG_TESTBRUKER)
const loadingSelectorPdlf = createLoadingSelector(GET_TESTBRUKER_PERSONOPPSLAG)
const loadingSelectorArena = createLoadingSelector(GET_ARENA_TESTBRUKER)
const loadingSelectorInst = createLoadingSelector(GET_INST_TESTBRUKER)
const loadingSelectorUdi = createLoadingSelector(GET_UDI_TESTBRUKER)

const loadingSelector = createSelector(
	state => state.loading,
	loading => {
		return {
			krrstub: loadingSelectorKrr({ loading }),
			sigrunstub: loadingSelectorSigrun({ loading }),
			aareg: loadingSelectorAareg({ loading }),
			pdlforvalter: loadingSelectorPdlf({ loading }),
			arenaforvalteren: loadingSelectorArena({ loading }),
			instdata: loadingSelectorInst({ loading }),
			udistub: loadingSelectorUdi({ loading })
		}
	}
)

const mapStateToProps = (state, ownProps) => (
	{
	loading: loadingSelector(state),
	testIdent: getIdentByIdSelector(state, ownProps.personId),
	data: getDataForIdent(state, ownProps.personId),
	bestilling: getBestillingById(state.bestillingStatuser.data, ownProps.bestillingId)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	getDataFraFagsystemer: () => dispatch(getDataFraFagsystemer(ownProps.personId)),
	frigjoerTestbruker: () =>
		dispatch(FRIGJOER_TESTBRUKER(ownProps.match.params.gruppeId, ownProps.personId)),
	editAction: () => dispatch(push(`${ownProps.match.url}/testbruker/${ownProps.personId}`))
})

export default withRouter(
	connect(
		mapStateToProps,
		mapDispatchToProps
	)(PersonVisning)
)
