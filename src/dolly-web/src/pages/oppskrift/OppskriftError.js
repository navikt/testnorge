import React from 'react'
import { Field } from 'formik'

const Error = ({ name }) => (
	<Field
		name={name}
		render={({ form: { touched, errors } }) =>
			touched[name] && errors[name] ? <span>{errors[name]}</span> : null
		}
	/>
)

export default Error
