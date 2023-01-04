import { connect } from 'react-redux'
import BestillingListe from './BestillingListe'

const mapStateToProps = (state, _ownProps) => ({
	searchActive: Boolean(state.search),
	navigerBestillingId: state.finnPerson.visBestilling,
	sidetall: state.finnPerson.sidetall,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
})

export default connect(mapStateToProps)(BestillingListe)
