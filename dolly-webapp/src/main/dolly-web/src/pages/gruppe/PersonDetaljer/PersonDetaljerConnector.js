import { connect } from 'react-redux'
import PersonDetaljer from './PersonDetaljer'
import DataMapper from '~/service/dataMapper'
import {
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
import Formatters from '~/utils/DataFormatter'

const loadingSelectorKrr = createLoadingSelector(GET_KRR_TESTBRUKER)
const loadingSelectorSigrun = createLoadingSelector(GET_SIGRUN_TESTBRUKER)
const loadingSelectorAareg = createLoadingSelector(GET_AAREG_TESTBRUKER)
const loadingSelectorPdlf = createLoadingSelector(GET_TESTBRUKER_PERSONOPPSLAG)
const loadingSelectorArena = createLoadingSelector(GET_ARENA_TESTBRUKER)
const loadingSelectorInst = createLoadingSelector(GET_INST_TESTBRUKER)
const loadingSelectorUdi = createLoadingSelector(GET_UDI_TESTBRUKER)
const loadingSelectorFrigjoer = createLoadingSelector(FRIGJOER_TESTBRUKER)

const mapStateToProps = (state, ownProps) => {
	return {
		isFetchingKrr: loadingSelectorKrr(state),
		isFetchingSigrun: loadingSelectorSigrun(state),
		isFetchingAareg: loadingSelectorAareg(state),
		isFetchingPdlf: loadingSelectorPdlf(state),
		isFetchingArena: loadingSelectorArena(state),
		isFetchingInst: loadingSelectorInst(state),
		isFetchingUdi: loadingSelectorUdi(state),
		isFrigjoering: loadingSelectorFrigjoer(state),
		personData: DataMapper.getDetailedData(state, ownProps.personId),
		testIdent: state.gruppe.data[0].testidenter.find(
			testIdent => testIdent.ident === ownProps.personId
		),
		bestilling: state.bestillingStatuser.data.find(
			bestilling => bestilling.id.toString() === Formatters.idUtenEllipse(ownProps.bestillingId)
		)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getKrrTestbruker: () => dispatch(GET_KRR_TESTBRUKER(ownProps.personId)),
		getSigrunTestbruker: () => dispatch(GET_SIGRUN_TESTBRUKER(ownProps.personId)),
		getSigrunSekvensnr: () => dispatch(GET_SIGRUN_SEKVENSNR(ownProps.personId)),
		getArenaTestbruker: () => dispatch(GET_ARENA_TESTBRUKER(ownProps.personId)),
		getAaregTestbruker: env => dispatch(GET_AAREG_TESTBRUKER(ownProps.personId, env)),
		getInstTestbruker: env => dispatch(GET_INST_TESTBRUKER(ownProps.personId, env)),
		getPdlfTestbruker: () => dispatch(GET_TESTBRUKER_PERSONOPPSLAG(ownProps.personId)),
		getUdiTestbruker: () => dispatch(GET_UDI_TESTBRUKER(ownProps.personId)),
		frigjoerTestbruker: () => dispatch(FRIGJOER_TESTBRUKER(ownProps.personId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(PersonDetaljer)
