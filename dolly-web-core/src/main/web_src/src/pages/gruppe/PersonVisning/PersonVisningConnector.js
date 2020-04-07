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
const loadingSelectorInntektstub = createLoadingSelector(actions.getInntektstub)
const loadingSelectorAareg = createLoadingSelector(actions.getAareg)
const loadingSelectorPdlf = createLoadingSelector(actions.getPDL)
const loadingSelectorArena = createLoadingSelector(actions.getArena)
const loadingSelectorInst = createLoadingSelector(actions.getInst)
const loadingSelectorUdi = createLoadingSelector(actions.getUdi)
const loadingSelectorSlettPerson = createLoadingSelector(actions.slettPerson)
const loadingSelectorPensjon = createLoadingSelector(actions.getPensjon)
// const loadingSelectorInntektsmelding = createLoadingSelector(actions.getInntektsmelding)

const loadingSelector = createSelector(
	state => state.loading,
	loading => {
		return {
			krrstub: loadingSelectorKrr({ loading }),
			sigrunstub: loadingSelectorSigrun({ loading }),
			inntektstub: loadingSelectorInntektstub({ loading }),
			aareg: loadingSelectorAareg({ loading }),
			pdlforvalter: loadingSelectorPdlf({ loading }),
			arenaforvalteren: loadingSelectorArena({ loading }),
			instdata: loadingSelectorInst({ loading }),
			udistub: loadingSelectorUdi({ loading }),
			slettPerson: loadingSelectorSlettPerson({ loading }),
			pensjonforvalter: loadingSelectorPensjon({ loading })
			// inntektsmelding: loadingSelectorInntektsmelding({ loading })
		}
	}
)

const mapStateToProps = (state, ownProps) => ({
	loading: loadingSelector(state),
	ident: selectIdentById(state, ownProps.personId),
	data: selectDataForIdent(state, ownProps.personId),
	bestilling: getBestillingById(state, ownProps.bestillingId)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	fetchDataFraFagsystemer: () => dispatch(fetchDataFraFagsystemer(ownProps.personId)),
	slettPerson: () => dispatch(actions.slettPerson(ownProps.personId)),
	leggTilPaaPerson: () =>
		dispatch(
			push(`/gruppe/${ownProps.match.params.gruppeId}/bestilling`, {
				leggTilPaaFnr: ownProps.personId
			})
		)
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(PersonVisning))
