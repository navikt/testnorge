import { connect } from 'react-redux'
import { getKodeverkSelector, fetchKodeverk } from '~/ducks/kodeverk'
import { KodeverkWrapper } from './Kodeverk'

// Koble til kodeverk
const mapStateToProps = (state, ownProps) => ({
	kodeverk: getKodeverkSelector(state, ownProps.navn)
})

const mapDispatchToProps = { fetchKodeverk }

export default connect(mapStateToProps, mapDispatchToProps)(KodeverkWrapper)
