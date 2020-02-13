import React, { useState } from 'react'
import Inntekt from './inntekt'
import { Formik } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import * as api from '../api'

const InntektStub = () => {
	const [fields, setFields] = useState({})
	console.log('fields :', fields)

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

export default InntektStub
