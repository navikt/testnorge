import { connect } from 'react-redux'
import { ArbeidsforholdForm } from './arbeidsforholdForm'

const mapStateToProps = state => ({
	brukerId: state.bruker.brukerData.brukerId
})

export default connect(mapStateToProps)(ArbeidsforholdForm)
