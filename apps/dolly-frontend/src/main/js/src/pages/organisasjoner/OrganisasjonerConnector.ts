// @ts-ignore
import { connect } from 'react-redux'
import { actions, fetchOrganisasjoner } from '~/ducks/organisasjon'
import { getOrganisasjonBestilling } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Organisasjoner from './Organisasjoner'
import { Dispatch } from 'redux'

const loadingSelector = createLoadingSelector([
	actions.getOrganisasjoner,
	actions.getOrganisasjonBestilling,
])

const mapStateToProps = (state: any) => ({
	state: state,
	search: state.search,
	isFetching: loadingSelector(state),
	bestillinger: state.organisasjon.bestillinger,
	organisasjoner: state.organisasjon.organisasjoner,
})

const mapDispatchToProps = (dispatch: Dispatch) => {
	return {
		getOrganisasjonBestillingStatus: (brukerId: string) =>
			dispatch(getOrganisasjonBestilling(brukerId)),
		getOrganisasjonBestilling: (brukerId: string) =>
			dispatch(actions.getOrganisasjonBestilling(brukerId)),
		getOrganisasjoner: (orgListe: Array<string>) => dispatch(actions.getOrganisasjoner(orgListe)),
		fetchOrganisasjoner: fetchOrganisasjoner(dispatch),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Organisasjoner)
