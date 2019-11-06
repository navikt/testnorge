import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { push } from 'connected-react-router'
import { getBestillingById } from '~/ducks/bestillingStatus'
import DataMapper from '~/service/dataMapper'
import {
	GET_KRR_TESTBRUKER,
	GET_SIGRUN_TESTBRUKER,
	GET_SIGRUN_SEKVENSNR,
	GET_AAREG_TESTBRUKER,
	GET_TESTBRUKER_PERSONOPPSLAG,
	GET_ARENA_TESTBRUKER,
	GET_INST_TESTBRUKER,
	GET_UDI_TESTBRUKER,
	getDataFraFagsystemer
} from '~/ducks/testBruker'
import { FRIGJOER_TESTBRUKER } from '~/ducks/testBruker'
import { createLoadingSelector } from '~/ducks/loading'
import Formatters from '~/utils/DataFormatter'
import PersonDetaljer from './PersonDetaljer'

const loadingSelectorKrr = createLoadingSelector(GET_KRR_TESTBRUKER)
const loadingSelectorSigrun = createLoadingSelector([GET_SIGRUN_TESTBRUKER, GET_SIGRUN_SEKVENSNR])
const loadingSelectorAareg = createLoadingSelector(GET_AAREG_TESTBRUKER)
const loadingSelectorPdlf = createLoadingSelector(GET_TESTBRUKER_PERSONOPPSLAG)
const loadingSelectorArena = createLoadingSelector(GET_ARENA_TESTBRUKER)
const loadingSelectorInst = createLoadingSelector(GET_INST_TESTBRUKER)
const loadingSelectorUdi = createLoadingSelector(GET_UDI_TESTBRUKER)
const loadingSelectorFrigjoer = createLoadingSelector(FRIGJOER_TESTBRUKER)

const mapStateToProps = (state, ownProps) => {
	return {
		username: state.bruker.brukerData.brukerId,
		isFetchingKrr: loadingSelectorKrr(state),
		isFetchingSigrun: loadingSelectorSigrun(state),
		isFetchingAareg: loadingSelectorAareg(state),
		isFetchingPdlf: loadingSelectorPdlf(state),
		isFetchingArena: loadingSelectorArena(state),
		isFetchingInst: loadingSelectorInst(state),
		isFetchingUdi: loadingSelectorUdi(state),
		isFrigjoering: loadingSelectorFrigjoer(state),
		personData: DataMapper.getDetailedData(state, ownProps.personId),
		testIdent: state.gruppe.data[0].identer.find(v => v.ident === ownProps.personId),
		bestilling: getBestillingById(
			state.bestillingStatuser.data,
			Formatters.idUtenEllipse(ownProps.bestillingId)
		)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getDataFraFagsystemer: () => dispatch(getDataFraFagsystemer(ownProps.personId)),
		frigjoerTestbruker: () =>
			dispatch(FRIGJOER_TESTBRUKER(ownProps.match.params.gruppeId, ownProps.personId)),
		editAction: () => dispatch(push(`${ownProps.match.url}/testbruker/${ownProps.personId}`))
	}
}

export default withRouter(
	connect(
		mapStateToProps,
		mapDispatchToProps
	)(PersonDetaljer)
)
