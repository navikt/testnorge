import { useEffect } from "react";
import * as React from "react";
import {ErrorMessage, useFormikContext} from 'formik'
import { ErrorMessageProps } from 'formik/dist/ErrorMessage'

const ErrorFocus = (): JSX.Element => {
    const { isSubmitting, isValidating, errors } = useFormikContext()

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
