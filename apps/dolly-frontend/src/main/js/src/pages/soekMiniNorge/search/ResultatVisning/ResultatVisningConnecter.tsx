// @ts-ignore
import { connect } from 'react-redux'
// @ts-ignore
import { withRouter } from 'react-router-dom'
import { createSelector } from 'reselect'
import { selectIdentById } from '~/ducks/gruppe'
import { fetchDataFraFagsystemerForSoek, selectDataForIdent, actions } from '~/ducks/fagsystem'
import { createLoadingSelector } from '~/ducks/loading'
import { ResultatVisning } from './ResultatVisning'

const loadingSelectorKrr = createLoadingSelector(actions.getKrr)
const loadingSelectorSigrun = createLoadingSelector([actions.getSigrun, actions.getSigrunSekvensnr])
const loadingSelectorInntektstub = createLoadingSelector(actions.getInntektstub)
const loadingSelectorAareg = createLoadingSelector(actions.getAareg)
const loadingSelectorPdlf = createLoadingSelector(actions.getPDL)
const loadingSelectorArena = createLoadingSelector(actions.getArena)
const loadingSelectorInst = createLoadingSelector(actions.getInst)
const loadingSelectorUdi = createLoadingSelector(actions.getUdi)
const loadingSelectorPensjon = createLoadingSelector(actions.getPensjon)

const loadingSelector = createSelector(
	// @ts-ignore
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
			pensjonforvalter: loadingSelectorPensjon({ loading })
		}
	}
)

const mapStateToProps = (state: any, ownProps: any) => ({
	loading: loadingSelector(state),
	data: selectDataForIdent(state, ownProps.personId),
	dataFraMiniNorge: ownProps.data
})

const mapDispatchToProps = (dispatch: any, ownProps: any) => ({
	fetchDataFraFagsystemerForSoek: () => dispatch(fetchDataFraFagsystemerForSoek(ownProps.personId))
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ResultatVisning))
