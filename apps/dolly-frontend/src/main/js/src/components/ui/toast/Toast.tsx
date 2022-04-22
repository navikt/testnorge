import React from 'react'
import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

type Props = {
	applicationError: Object
	clearAllErrors: () => void
}

export const Toast = ({ applicationError, clearAllErrors }: Props) => {
	if (!applicationError) {
		return null
	}

	toast.error(applicationError, {
		position: toast.POSITION.BOTTOM_RIGHT,
		onClose: clearAllErrors,
		autoClose: 10000,
		pauseOnHover: true,
		type: 'error',
	})

	return <ToastContainer />
}
