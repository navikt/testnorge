//@ts-ignore
import { connect } from 'react-redux'
import { actions, sokSelector, selectOrgListe, fetchOrganisasjoner } from '~/ducks/organisasjon'
import { getOrganisasjonBestilling } from '~/ducks/bestillingStatus'
import OrganisasjonListe from './OrganisasjonListe'

const mapStateToProps = (state: any) => ({
	organisasjoner: sokSelector(selectOrgListe(state), state.search),
	bestillinger: state.organisasjon.bestillinger
})

const mapDispatchToProps = (dispatch: React.Dispatch<React.SetStateAction<string>>) => ({
	getOrganisasjonBestillingStatus: (brukerId: string) =>
		dispatch(getOrganisasjonBestilling(brukerId)),
	getOrganisasjonBestilling: (brukerId: string) =>
		dispatch(actions.getOrganisasjonBestilling(brukerId)),
	fetchOrganisasjoner: fetchOrganisasjoner(dispatch)
})

export default connect(mapStateToProps, mapDispatchToProps)(OrganisasjonListe)
