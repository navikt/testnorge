import { connect } from 'react-redux'
import { EgenOrganisasjonSelect } from './EgenOrganisasjonSelect'

const mapStateToProps = state => ({
	brukerId: state.bruker.brukerData.brukerId
})

export default connect(mapStateToProps)(EgenOrganisasjonSelect)
