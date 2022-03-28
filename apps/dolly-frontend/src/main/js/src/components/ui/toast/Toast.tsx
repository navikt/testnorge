import React from 'react'
import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import { REQUEST_ERROR } from '~/service/services/Request'
import { applicationErrorSelector } from '~/ducks/errors'

export const Toast = (clearErrors: any) => {
	const error = applicationErrorSelector(sessionStorage.getItem(REQUEST_ERROR))
	console.log('error: ', error) //TODO - SLETT MEG
	const notifyError = () =>
		toast.error(error, {
			position: toast.POSITION.BOTTOM_RIGHT,
			onClose: clearErrors,
			autoClose: 10000,
		})
	if (error) {
		return (
			<>
				<ToastContainer />
				{notifyError()}
			</>
		)
	}
}
