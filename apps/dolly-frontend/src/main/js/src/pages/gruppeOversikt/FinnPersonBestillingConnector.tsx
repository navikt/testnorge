import { connect } from 'react-redux'
import { Action } from 'redux-actions'
import {
	navigerTilBestilling,
	navigerTilPerson,
	resetFeilmelding,
	setVisning,
} from '~/ducks/finnPerson'
import FinnPersonBestilling from '~/pages/gruppeOversikt/FinnPersonBestilling'
import { VisningType } from '~/pages/gruppe/Gruppe'

const mapStateToProps = (state: {
	finnPerson: { feilmelding: string; navigerTilGruppe: number }
}) => ({
	feilmelding: state.finnPerson.feilmelding,
	gruppe: state.finnPerson.navigerTilGruppe,
})

const mapDispatchToProps = (dispatch: (arg0: Action<any>) => any) => ({
	navigerTilPerson: (ident: string) => {
		dispatch(navigerTilPerson(ident))
		dispatch(setVisning(VisningType.VISNING_PERSONER))
	},
	navigerTilBestilling: (bestillingId: string) => {
		dispatch(navigerTilBestilling(bestillingId))
		dispatch(setVisning(VisningType.VISNING_BESTILLING))
	},
	resetFeilmelding: () => dispatch(resetFeilmelding()),
})

export default connect(mapStateToProps, mapDispatchToProps)(FinnPersonBestilling)
