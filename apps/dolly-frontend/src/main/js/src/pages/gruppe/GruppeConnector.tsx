import { connect } from 'react-redux'
import Gruppe from './Gruppe'

const mapStateToProps = (
	state: {
		visning: string
		finnPerson: {
			visning: string
			sidetall: number
			sideStoerrelse: number
			sorting: string
			update: string
		}
	},
	ownProps: any,
) => ({
	...ownProps,
	visning: state.finnPerson.visning,
	sidetall: state.finnPerson.sidetall,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
	sorting: state.finnPerson.sorting,
	update: state.finnPerson.update,
})

// @ts-ignore
export default connect(mapStateToProps)(Gruppe)
