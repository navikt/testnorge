import React, { useState } from 'react'
import Inntekt from './inntekt'
import { Formik } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import * as api from './api'

const InntektFrom = () => {
	const [fields, setFields] = useState({})
	console.log('fields :', fields)

	const validate = values => {
		const errors = {}
		console.log('values :', values.inntektstype)
		//   if (!values.firstName) {
		//     errors.firstName = 'Required';
		//   } else if (values.firstName.length > 15) {
		//     errors.firstName = 'Must be 15 characters or less';
		//   }

		//   if (!values.lastName) {
		//     errors.lastName = 'Required';
		//   } else if (values.lastName.length > 20) {
		//     errors.lastName = 'Must be 20 characters or less';
		//   }

		//   if (!values.email) {
		//     errors.email = 'Required';
		//   } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)) {
		//     errors.email = 'Invalid email address';
		//   }

		return errors
	}

	return (
		<Formik
			initialValues={{}}
			// validateForm={validate}
			onSubmit={values => api.validate(values).then(response => setFields(response))}
			render={({ handleSubmit }) => (
				<div>
					<Inntekt fields={fields} onValidate={handleSubmit} />
					<NavButton type="hoved" onClick={values => validate(values)}>
						Opprett
					</NavButton>
				</div>
			)}
		/>
	)
}

export default InntektFrom
