import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { createSelector } from 'reselect'
import { push } from 'connected-react-router'
import { getBestillingById, getBestillingsListe } from '~/ducks/bestillingStatus'
import { selectIdentById } from '~/ducks/gruppe'
import { actions, fetchDataFraFagsystemer, selectDataForIdent } from '~/ducks/fagsystem'
import { createLoadingSelector } from '~/ducks/loading'
import { PersonVisning } from './PersonVisning'

const loadingSelectorKrr = createLoadingSelector(actions.getKrr)
const loadingSelectorSigrun = createLoadingSelector([actions.getSigrun, actions.getSigrunSekvensnr])
const loadingSelectorInntektstub = createLoadingSelector(actions.getInntektstub)
const loadingSelectorAareg = createLoadingSelector(actions.getAareg)
const loadingSelectorPdl = createLoadingSelector(actions.getPDL)
const loadingSelectorPdlForvalter = createLoadingSelector(actions.getPdlForvalter)
const loadingSelectorArena = createLoadingSelector(actions.getArena)
const loadingSelectorInst = createLoadingSelector(actions.getInst)
const loadingSelectorUdi = createLoadingSelector(actions.getUdi)
const loadingSelectorSlettPerson = createLoadingSelector(actions.slettPerson)
const loadingSelectorPensjon = createLoadingSelector(actions.getPensjon)
const loadingSelectorBrregstub = createLoadingSelector(actions.getBrreg)

const loadingSelector = createSelector(
	(state) => state.loading,
	(loading) => {
		return {
			krrstub: loadingSelectorKrr({ loading }),
			sigrunstub: loadingSelectorSigrun({ loading }),
			inntektstub: loadingSelectorInntektstub({ loading }),
			aareg: loadingSelectorAareg({ loading }),
			pdl: loadingSelectorPdl({ loading }),
			pdlforvalter: loadingSelectorPdlForvalter({ loading }),
			arenaforvalteren: loadingSelectorArena({ loading }),
			instdata: loadingSelectorInst({ loading }),
			udistub: loadingSelectorUdi({ loading }),
			slettPerson: loadingSelectorSlettPerson({ loading }),
			pensjonforvalter: loadingSelectorPensjon({ loading }),
			brregstub: loadingSelectorBrregstub({ loading }),
		}
	}
)

const mapStateToProps = (state, ownProps) => ({
	loading: loadingSelector(state),
	ident: selectIdentById(state, ownProps.personId),
	data: selectDataForIdent(state, ownProps.personId),
	bestilling: getBestillingById(state, ownProps.bestillingId),
	bestillingsListe: getBestillingsListe(state, ownProps.bestillingsIdListe),
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	fetchDataFraFagsystemer: () => dispatch(fetchDataFraFagsystemer(ownProps.personId)),
	slettPerson: () => dispatch(actions.slettPerson(ownProps.personId)),
	leggTilPaaPerson: (data, bestillinger, master, type) => {
		return dispatch(
			push(`/gruppe/${ownProps.match.params.gruppeId}/bestilling/${ownProps.personId}`, {
				personFoerLeggTil: data,
				tidligereBestillinger: bestillinger,
				identMaster: master,
				identtype: type,
			})
		)
	},
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(PersonVisning))
