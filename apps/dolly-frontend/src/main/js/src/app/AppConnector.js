import { connect } from 'react-redux'
import { updateVarslingerBruker } from '~/ducks/varslinger'
import { App } from '~/app/App'

const mapDispatchToProps = (dispatch) => ({
	updateVarslingerBruker: (varslingId) => dispatch(updateVarslingerBruker(varslingId)),
})

export default connect(mapDispatchToProps)(App)
