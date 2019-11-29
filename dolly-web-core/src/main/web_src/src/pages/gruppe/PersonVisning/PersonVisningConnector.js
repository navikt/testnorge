import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { createSelector } from 'reselect'
import { push } from 'connected-react-router'
import { getBestillingById } from '~/ducks/bestillingStatus'
import { selectIdentById } from '~/ducks/gruppe'
import { fetchDataFraFagsystemer, selectDataForIdent, actions } from '~/ducks/fagsystem'
import { createLoadingSelector } from '~/ducks/loading'
import { PersonVisning } from './PersonVisning'

const loadingSelectorKrr = createLoadingSelector(actions.getKrr)
const loadingSelectorSigrun = createLoadingSelector([actions.getSigrun, actions.getSigrunSekvensnr])
const loadingSelectorAareg = createLoadingSelector(actions.getAareg)
const loadingSelectorPdlf = createLoadingSelector(actions.getPDL)
const loadingSelectorArena = createLoadingSelector(actions.getArena)
const loadingSelectorInst = createLoadingSelector(actions.getInst)
const loadingSelectorUdi = createLoadingSelector(actions.getUdi)

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

const mapStateToProps = (state, ownProps) => ({
	loading: loadingSelector(state),
	testIdent: selectIdentById(state, ownProps.personId),
	data: selectDataForIdent(state, ownProps.personId),
	bestilling: getBestillingById(state.bestillingStatuser.data, ownProps.bestillingId)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	fetchDataFraFagsystemer: () => dispatch(fetchDataFraFagsystemer(ownProps.personId)),
	frigjoerTestbruker: () =>
		dispatch(actions.frigjoerTestbruker(ownProps.match.params.gruppeId, ownProps.personId)),
	editAction: () => dispatch(push(`${ownProps.match.url}/testbruker/${ownProps.personId}`))
})

export default withRouter(
	connect(
		mapStateToProps,
		mapDispatchToProps
	)(PersonVisning)
)
