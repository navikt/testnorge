import { connect } from 'react-redux'
import UtenFastBopel from './UtenFastBopel'

const mapStateToProps = state => ({
	attributeIds: state.currentBestilling.attributeIds,
	values: state.currentBestilling.values
})

export default connect(mapStateToProps)(UtenFastBopel)
