import { connect } from 'react-redux'
import { createSelector } from 'reselect'
import { actions, fetchDataFraFagsystemer, selectDataForIdent } from '~/ducks/fagsystem'
import { createLoadingSelector } from '~/ducks/loading'
import { PersonVisning } from './PersonVisning'
import { increaseAntallFjernet, decreaseAntallFjernet } from '~/ducks/redigertePersoner'

const loadingSelectorKrr = createLoadingSelector(actions.getKrr)
const loadingSelectorSigrun = createLoadingSelector([actions.getSigrun, actions.getSigrunSekvensnr])
const loadingSelectorInntektstub = createLoadingSelector(actions.getInntektstub)
const loadingSelectorAareg = createLoadingSelector(actions.getAareg)
const loadingSelectorPdlForvalter = createLoadingSelector(actions.getPdlForvalter)
const loadingSelectorArena = createLoadingSelector(actions.getArena)
const loadingSelectorInst = createLoadingSelector(actions.getInst)
const loadingSelectorUdi = createLoadingSelector(actions.getUdi)
const loadingSelectorSlettPerson = createLoadingSelector(actions.slettPerson)
const loadingSelectorSlettPersonOgPartner = createLoadingSelector(actions.slettPersonOgPartner)
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
			slettPersonOgPartner: loadingSelectorSlettPersonOgPartner({ loading }),
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
			dispatch(increaseAntallFjernet())
			return dispatch(actions.slettPerson(ownProps.personId))
		},
		slettPersonOgPartner: (partnerident) => {
			dispatch(increaseAntallFjernet())
			dispatch(increaseAntallFjernet())
			return dispatch(actions.slettPersonOgPartner(ownProps.personId, partnerident))
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
		updateAntallImporterte: () => {
			dispatch(decreaseAntallFjernet())
		},
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(PersonVisning)
