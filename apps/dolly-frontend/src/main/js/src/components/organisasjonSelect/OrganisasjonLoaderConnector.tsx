import { connect } from 'react-redux'
import { Dispatch } from 'redux'
import { Organisasjon } from '~/service/services/organisasjonFasteDataService/OrganisasjonFasteDataService'
import { actions } from '~/ducks/fastedata'
import { OrganisasjonLoader } from '~/components/organisasjonSelect/OrganisasjonLoader'

const mapStateToProps = (state: { fasteData: { organisasjoner: Organisasjon[] } }) => ({
	organisasjoner: state?.fasteData?.organisasjoner,
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	hentOrganisasjoner: (gruppe: string, kanHaArbeidsforhold: boolean) =>
		dispatch(actions.getFastedataOrganisasjoner(gruppe, kanHaArbeidsforhold)),
})

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(OrganisasjonLoader)
