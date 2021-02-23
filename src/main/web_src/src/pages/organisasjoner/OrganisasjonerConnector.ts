// @ts-ignore
import { connect } from 'react-redux'
import { actions, fetchOrganisasjoner } from '~/ducks/organisasjon'
import { getOrganisasjonBestilling } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Organisasjoner from './Organisasjoner'

const loadingSelectorBestillinger = createLoadingSelector(actions.getOrganisasjonBestilling)
const loadingSelectorOrg = createLoadingSelector(actions.getOrganisasjoner)

const mapStateToProps = (state: any) => ({
	isFetchingBestillinger: loadingSelectorBestillinger(state),
	isFetchingOrg: loadingSelectorOrg(state),
	bestillinger: state.organisasjon.bestillinger,
	organisasjoner: state.organisasjon.organisasjoner,
	brukerId: state.bruker.brukerData.brukerId
})

const mapDispatchToProps = (dispatch: React.Dispatch<React.SetStateAction<string>>) => {
	return {
		getOrganisasjonBestillingStatus: (brukerId: string) =>
			dispatch(actions.getOrganisasjonBestilling(brukerId)),
		getOrganisasjonBestilling: (brukerId: string) => dispatch(getOrganisasjonBestilling(brukerId)),
		getOrganisasjoner: (orgListe: Array<string>) => dispatch(actions.getOrganisasjoner(orgListe)),
		fetchOrganisasjoner: fetchOrganisasjoner(dispatch)
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Organisasjoner)
