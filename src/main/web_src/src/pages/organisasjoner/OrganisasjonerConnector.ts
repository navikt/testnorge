// @ts-ignore
import { connect } from 'react-redux'
import { actions } from '~/ducks/organisasjon'
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

const mapDispatchToProps = (dispatch: React.Dispatch<React.SetStateAction<string>>) => {
	const uri = `${config.services.dollyBackend}`
	return {
		getOrganisasjoner: () =>
			dispatch(actions.getOrganisasjoner(`${uri}/orgnummer`, { method: 'GET' }))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Organisasjoner)
