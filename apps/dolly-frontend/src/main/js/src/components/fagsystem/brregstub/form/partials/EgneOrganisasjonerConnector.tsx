import { connect } from 'react-redux'
import { Dispatch } from 'redux'
import { Organisasjon } from '~/service/services/organisasjonFasteDataService/OrganisasjonFasteDataService'
import { createLoadingSelector } from '~/ducks/loading'
import { actions } from '~/ducks/organisasjon'
import { EgneOrganisasjoner } from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'

const loadingSelector = createLoadingSelector([actions.getOrganisasjonerPaaBruker])

const mapStateToProps = (state: { organisasjon: { egneOrganisasjoner: Organisasjon[] } }) => ({
	organisasjoner: state?.organisasjon?.egneOrganisasjoner,
	isLoading: loadingSelector(state),
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	hentOrganisasjoner: (brukerId: string) => dispatch(actions.getOrganisasjonerPaaBruker(brukerId)),
})

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(EgneOrganisasjoner)
