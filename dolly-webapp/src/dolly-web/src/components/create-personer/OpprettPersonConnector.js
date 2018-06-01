import { connect } from 'react-redux'
import OpprettPerson from './OpprettePerson'

const mapStateToProps = state => ({
	grupperState: state.grupper
})

export default connect(mapStateToProps)(OpprettPerson)
