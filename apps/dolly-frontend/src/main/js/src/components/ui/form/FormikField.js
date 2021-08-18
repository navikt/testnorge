import React from 'react'
import { Field, FastField } from 'formik'

export const FormikField = ({ fastfield = true, children, ...props }) => {
	const FieldComponent = fastfield ? FastField : Field
	return <FieldComponent {...props}>{children}</FieldComponent>
}
