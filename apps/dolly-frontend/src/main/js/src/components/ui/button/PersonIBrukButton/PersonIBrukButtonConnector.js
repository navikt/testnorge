import { connect } from 'react-redux'
import { actions } from '~/ducks/gruppe'
import { PersonIBrukButton } from './PersonIBrukButton'

const mapDispatchToProps = {
	updateIdentIbruk: actions.updateIdentIbruk,
}

export default connect(null, mapDispatchToProps)(PersonIBrukButton)
