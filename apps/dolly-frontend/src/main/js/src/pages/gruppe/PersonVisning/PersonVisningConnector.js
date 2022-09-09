import { connect } from 'react-redux'
import { createSelector } from 'reselect'
import { actions, fetchDataFraFagsystemer, selectDataForIdent } from '~/ducks/fagsystem'
import { createLoadingSelector } from '~/ducks/loading'
import { PersonVisning } from './PersonVisning'

const loadingSelectorKrr = createLoadingSelector(actions.getKrr)
const loadingSelectorSigrun = createLoadingSelector([actions.getSigrun, actions.getSigrunSekvensnr])
const loadingSelectorInntektstub = createLoadingSelector(actions.getInntektstub)
const loadingSelectorAareg = createLoadingSelector(actions.getAareg)
const loadingSelectorPdlForvalter = createLoadingSelector(actions.getPdlForvalter)
const loadingSelectorArena = createLoadingSelector(actions.getArena)
const loadingSelectorInst = createLoadingSelector(actions.getInst)
const loadingSelectorUdi = createLoadingSelector(actions.getUdi)
const loadingSelectorSlettPerson = createLoadingSelector(actions.slettPerson)
const loadingSelectorSlettPersonOgRelatertePersoner = createLoadingSelector(
	actions.slettPersonOgRelatertePersoner
)
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
			pdlforvalter: loadingSelectorPdlForvalter({ loading }),
			arenaforvalteren: loadingSelectorArena({ loading }),
			instdata: loadingSelectorInst({ loading }),
			udistub: loadingSelectorUdi({ loading }),
			slettPerson: loadingSelectorSlettPerson({ loading }),
			slettPersonOgRelatertePersoner: loadingSelectorSlettPersonOgRelatertePersoner({ loading }),
			pensjonforvalter: loadingSelectorPensjon({ loading }),
			brregstub: loadingSelectorBrregstub({ loading }),
		}
	}
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
		slettPersonOgRelatertePersoner: (relatertPersonIdenter) => {
			return dispatch(
				actions.slettPersonOgRelatertePersoner(ownProps.personId, relatertPersonIdenter)
			)
		},
		leggTilPaaPerson: (data, bestillinger, master, type, gruppeId, navigate) =>
			navigate(`/gruppe/${gruppeId}/bestilling/${ownProps.personId}`, {
				state: {
					personFoerLeggTil: data,
					tidligereBestillinger: bestillinger,
					identMaster: master,
					identtype: type,
				},
			}),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(PersonVisning)
