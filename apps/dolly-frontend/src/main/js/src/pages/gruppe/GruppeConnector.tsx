import { connect } from 'react-redux'
import Gruppe, { VisningType } from './Gruppe'
import { FormikProps } from 'formik'
import { Dispatch } from 'redux'
import { setVisning } from '~/ducks/finnPerson'

const mapStateToProps = (
	state: {
		visning: string
		finnPerson: {
			visning: string
		}
		redigertePersoner: {
			antallPersonerFjernet: number
		}
	},
	ownProps: FormikProps<any>
) => ({
	...ownProps,
	visning: state.finnPerson.visning,
	antallPersonerFjernet: state.redigertePersoner.antallPersonerFjernet,
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
	setVisning: (visning: VisningType) => dispatch(setVisning(visning)),
})

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(Gruppe)
