import React from 'react'
import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import { applicationErrorSelector } from '~/ducks/errors'
import { useStore } from 'react-redux'

export const Toast = ({ clearErrors }: { clearErrors: any }) => {
	const state = useStore().getState()
	const error = applicationErrorSelector(state)

	if (!error) {
		return null
	}
	const notifyError = () =>
		toast.error(error, {
			position: toast.POSITION.BOTTOM_RIGHT,
			onClose: clearErrors,
			autoClose: 10000,
		})
	return (
		<>
			<ToastContainer />
			{notifyError()}
		</>
	)
}
