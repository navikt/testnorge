import { connect } from 'react-redux'
import Gruppe, { VisningType } from './Gruppe'
import { FormikProps } from 'formik'
import { Dispatch } from 'redux'
import { setVisning } from '@/ducks/finnPerson'

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
	ownProps: FormikProps<any>
) => ({
	...ownProps,
	visning: state.finnPerson.visning,
	sidetall: state.finnPerson.sidetall,
	sideStoerrelse: state.finnPerson.sideStoerrelse,
	sorting: state.finnPerson.sorting,
	update: state.finnPerson.update,
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	setVisning: (visning: VisningType) => dispatch(setVisning(visning)),
})

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(Gruppe)
