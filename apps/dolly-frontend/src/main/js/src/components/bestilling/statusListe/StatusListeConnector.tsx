import { connect, RootStateOrAny } from 'react-redux'
import { createLoadingSelector } from '~/ducks/loading'
import { cancelBestilling } from '~/ducks/bestillingStatus'
import StatusListe from './StatusListe'
import { Dispatch } from 'redux'

const loadingCancelSelector = createLoadingSelector(cancelBestilling)

const mapStateToProps = (state: RootStateOrAny) => ({
	isCanceling: loadingCancelSelector(state),
})

const mapDispatchToProps = (dispatch: Dispatch) => {
	return {
		cancelBestilling: (bestillingId: number, erOrganisasjon = false) =>
			dispatch(cancelBestilling(bestillingId, erOrganisasjon)),
	}
}

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(StatusListe)
