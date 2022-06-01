import { connect } from 'react-redux'
import GruppeOversikt from './GruppeOversikt'

const mapStateToProps = (state, ownProps) => ({
	searchActive: Boolean(state.search),
	sidetall: state.finnPerson.sidetall,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
})

export default connect(mapStateToProps)(GruppeOversikt)
