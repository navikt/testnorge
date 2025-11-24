import { connect } from 'react-redux'
import { createSelector } from 'reselect'
import { actions, fetchDataFraFagsystemer, selectDataForIdent } from '@/ducks/fagsystem'
import { createLoadingSelector } from '@/ducks/loading'
import PersonVisning from '@/pages/gruppe/PersonVisning/PersonVisning'

const loadingSelectorKrr = createLoadingSelector(actions.getKrr)
const loadingSelectorInntektstub = createLoadingSelector(actions.getInntektstub)
const loadingSelectorPdlForvalter = createLoadingSelector(actions.getPdlForvalter)
const loadingSelectorSlettPerson = createLoadingSelector(actions.slettPerson)

const loadingSelectorBrregstub = createLoadingSelector(actions.getBrreg)
const loadingSelectorTpsMessaging = createLoadingSelector(actions.getTpsMessaging)
const loadingSelectorKontoregister = createLoadingSelector(actions.getKontoregister)

const loadingSelector = createSelector(
	(state) => state.loading,
	(loading) => {
		return {
			krrstub: loadingSelectorKrr({ loading }),
			inntektstub: loadingSelectorInntektstub({ loading }),
			pdlforvalter: loadingSelectorPdlForvalter({ loading }),
			slettPerson: loadingSelectorSlettPerson({ loading }),
			brregstub: loadingSelectorBrregstub({ loading }),
			tpsMessaging: loadingSelectorTpsMessaging({ loading }),
			kontoregister: loadingSelectorKontoregister({ loading }),
		}
	},
)

const mapStateToProps = (state, ownProps) => ({
	loading: loadingSelector(state),
	data: selectDataForIdent(state, ownProps.personId),
	tmpPersoner: state.redigertePersoner,
})

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		fetchDataFraFagsystemer: (bestillinger) =>
			dispatch(fetchDataFraFagsystemer(ownProps.ident, bestillinger)),
		slettPerson: () => {
			return dispatch(actions.slettPerson(ownProps.personId))
		},
		leggTilPaaPerson: (data, bestillinger, master, type, gruppeId, navigate) =>
			navigate(`/gruppe/${gruppeId}/bestilling/${ownProps.personId}`, {
				state: {
					personFoerLeggTil: data,
					tidligereBestillinger: bestillinger,
					identMaster: master,
					identtype: type,
					timedOutFagsystemer: data?.timedOutFagsystemer,
				},
			}),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(PersonVisning)
