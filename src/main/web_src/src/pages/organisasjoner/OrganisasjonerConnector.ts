// @ts-ignore
import { connect } from 'react-redux'
import { actions, fetchOrganisasjoner } from '~/ducks/organisasjon'
import { getOrganisasjonBestilling } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Organisasjoner from './Organisasjoner'

const loadingSelector = createLoadingSelector(actions.getOrganisasjoner)

const mapStateToProps = (state: any) => {
	return {
		isFetching: loadingSelector(state),
		organisasjoner: state.organisasjon.organisasjoner,
		brukerId: state.bruker.brukerData.brukerId,
		brukernavn: state.bruker.brukerData.brukernavn
	}
}

const mapDispatchToProps = (dispatch: React.Dispatch<React.SetStateAction<string>>) => {
	return {
		fetchOrganisasjoner,
		getOrganisasjonBestilling: brukerId => dispatch(getOrganisasjonBestilling(brukerId)),
		getOrganisasjoner: orgListe => dispatch(actions.getOrganisasjoner(orgListe))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Organisasjoner)
