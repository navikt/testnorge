import { connect } from 'react-redux'
import { Action } from 'redux-actions'
import { navigerTilBestilling, navigerTilPerson, resetFeilmelding } from '~/ducks/finnPerson'
import FinnPersonBestilling from '~/pages/gruppeOversikt/FinnPersonBestilling'

const mapStateToProps = (state: {
	finnPerson: { feilmelding: string; navigerTilGruppe: number }
}) => ({
	feilmelding: state.finnPerson.feilmelding,
	gruppe: state.finnPerson.navigerTilGruppe,
})

const mapDispatchToProps = (dispatch: (arg0: Action<any>) => any) => ({
	navigerTilPerson: (ident: string) => {
		dispatch(navigerTilPerson(ident))
	},
	navigerTilBestilling: (bestillingId: string) => {
		dispatch(navigerTilBestilling(bestillingId))
	},
	resetFeilmelding: () => dispatch(resetFeilmelding()),
})

export default connect(mapStateToProps, mapDispatchToProps)(FinnPersonBestilling)
