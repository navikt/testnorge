// @ts-ignore
import { connect } from 'react-redux'
import { actions, fetchOrganisasjoner } from '~/ducks/organisasjon'
import { getOrganisasjonBestilling } from '~/ducks/bestillingStatus'
import { createLoadingSelector } from '~/ducks/loading'
import Organisasjoner from './Organisasjoner'
import config from '~/config'

const loadingSelector = createLoadingSelector(actions.getOrganisasjoner)

const mapStateToProps = (state: any) => {
	return {
		isFetching: loadingSelector(state),
		organisasjoner: state.organisasjon.organisasjoner,
		brukerId: state.bruker.brukerData.brukerId,
		brukernavn: state.bruker.brukerData.brukernavn
	}
}

const mapDispatchToProps = (dispatch: React.Dispatch<React.SetStateAction<string>>, ownProps) => {
	return {
		fetchOrganisasjoner,
		// TODO: Få disse to til å hente bruker-id og org.nr.liste, slik at de kan brukes i Organisasjoner.tsx
		getOrganisasjonBestilling: () =>
			dispatch(getOrganisasjonBestilling('952ab92e-926f-4ac4-93d7-f2d552025caf')),
		getOrganisasjoner: () => dispatch(actions.getOrganisasjoner(['958741839']))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Organisasjoner)
