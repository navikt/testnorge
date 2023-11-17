import * as React from 'react'
import { useEffect } from 'react'
import { ErrorMessage } from 'formik'
import { ErrorMessageProps } from 'formik/dist/ErrorMessage'
import { useFormContext } from 'react-hook-form'

const ErrorFocus = (): JSX.Element | null => {
	const {
		formState: { isSubmitting, isValidating, errors },
	} = useFormContext()

	useEffect(() => {
		const keys = Object.keys(errors)

		if (keys.length > 0 && isSubmitting && !isValidating) {
			const errorElement = document.querySelector(`div[class="error-message"]`)

			if (errorElement) {
				errorElement.scrollIntoView({ behavior: 'smooth' })
			}
		}
	}, [isSubmitting, isValidating, errors])
	return null
}

export const ErrorMessageWithFocus = (props: ErrorMessageProps): JSX.Element => {
	return (
		<>
			<ErrorMessage {...props} />
			<ErrorFocus />
		</>
	)
}
