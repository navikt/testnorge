//@ts-ignore
import { connect } from 'react-redux'
import { sokSelector, selectOrgListe } from '~/ducks/organisasjon'
import OrganisasjonListe from './OrganisasjonListe'

const mapStateToProps = (state: any) => ({
	organisasjoner: sokSelector(selectOrgListe(state), state.search),
	bestillinger: state.organisasjon.bestillinger
})

export default connect(mapStateToProps)(OrganisasjonListe)
