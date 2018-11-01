import { connect } from 'react-redux'
import MiljoVelger from './MiljoVelger'

const mapStateToProps = state => ({
	environments: state.environments.data
})

export default connect(mapStateToProps)(MiljoVelger)
