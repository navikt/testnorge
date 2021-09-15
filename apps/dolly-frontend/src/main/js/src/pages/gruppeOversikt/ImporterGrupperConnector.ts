//@ts-ignore
import { connect } from 'react-redux'
import { actions } from '~/ducks/gruppe'
import ImporterGrupper from './ImporterGrupper'

const mapDispatchToProps = (dispatch: React.Dispatch<React.SetStateAction<string>>) => {
	return {
		importZIdent: (ZIdent: string) => dispatch(actions.importZIdent(ZIdent)),
	}
}

export default connect(null, mapDispatchToProps)(ImporterGrupper)
