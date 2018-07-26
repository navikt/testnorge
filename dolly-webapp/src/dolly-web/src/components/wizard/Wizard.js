import React from 'react'
import { Formik, Field } from 'formik'
import StepIndicator from './StepIndicator'
import DisplayFormikState from '~/utils/DisplayFormikState'
import FormErrors from '~/components/formErrors/FormErrors'

export default class Wizard extends React.Component {
	static Page = ({ children }) => children

	constructor(props) {
		super(props)

		this.state = {
			page: 0,
			values: props.initialValues
		}
	}

	next = values =>
		this.setState(state => ({
			page: Math.min(state.page + 1, this.props.children.length - 1),
			values
		}))

	previous = () =>
		this.setState(state => ({
			page: Math.max(state.page - 1, 0)
		}))

	validate = values => {
		const activePage = React.Children.toArray(this.props.children)[this.state.page]

		return activePage.props.validate ? activePage.props.validate(values) : {}
	}

	handleSubmit = (values, bag) => {
		const { children, onSubmit } = this.props

		const { page } = this.state

		const isLastPage = page === React.Children.count(children) - 1

		if (isLastPage) {
			return onSubmit(values)
		} else {
			this.next(values)

			bag.setSubmitting(false)
		}
	}

	render() {
		const { children, validationSchemaList } = this.props

		const { page, values } = this.state

		const validationSchema = validationSchemaList[page]

		const activePage = React.Children.toArray(children)[page].props.children

		const isLastPage = page === React.Children.count(children) - 1

		return (
			<Formik
				initialValues={values}
				enableReinitialize={false}
				validationSchema={validationSchema}
				onSubmit={this.handleSubmit}
				render={props => {
					const { values, touched, errors, dirty, handleSubmit, isSubmitting, handleReset } = props
					return (
						<div className="oppskrift-page">
							<StepIndicator activeStep={page} />
							<form onSubmit={handleSubmit}>
								{React.cloneElement(activePage, { values, errors, touched })}

								<div className="buttons">
									{page > 0 && (
										<button type="button" onClick={this.previous}>
											« Previous
										</button>
									)}

									{!isLastPage && <button type="submit">Next »</button>}

									{isLastPage && (
										<button type="submit" disabled={isSubmitting}>
											Submit
										</button>
									)}
								</div>

								<FormErrors errors={errors} touched={touched} />
								<DisplayFormikState {...props} />
							</form>
						</div>
					)
				}}
			/>
		)
	}
}
