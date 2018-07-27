import React from 'react'
import { Formik, Field } from 'formik'
import StepIndicator from './StepIndicator'
import DisplayFormikState from '~/utils/DisplayFormikState'
import FormErrors from '~/components/formErrors/FormErrors'
import Knapp from 'nav-frontend-knapper'
import Button from '~/components/button/Button'
import NavButton from '~/components/button/NavButton/NavButton'
import Icon from '~/components/icon/Icon'

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

			bag.resetForm(values)
			bag.setSubmitting(false)
		}
	}

	render() {
		const { children, validationSchemaList, onCancelHandler } = this.props

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
								{React.cloneElement(activePage, { values })}

								<div className="oppskrift-knapper">
									<Knapp type="standard" onClick={onCancelHandler}>
										AVBRYT
									</Knapp>

									<div className="oppskrift-knapper_right">
										{page > 0 && (
											<NavButton direction="backward" type="button" onClick={this.previous} />
										)}

										{!isLastPage && <NavButton direction="forward" type="submit" />}

										{isLastPage && (
											<Knapp type="hoved" htmlType="submit" disabled={isSubmitting}>
												OPPRETT
											</Knapp>
										)}
									</div>
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
