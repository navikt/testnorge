import { connect } from 'react-redux'
import { EgenOrganisasjonSelect } from './EgenOrganisasjonSelect'

const mapStateToProps = (state: any) => ({
	brukerId: state.bruker.brukerData.brukerId
})

export default connect(mapStateToProps)(EgenOrganisasjonSelect)
