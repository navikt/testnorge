import React from 'react'
import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

type Props = {
	applicationError: string
}

export const Toast = ({ applicationError }: Props) => {
	if (!applicationError) {
		return null
	}

	toast.error(applicationError.replace(/\?\S*/, ''), {
		position: 'bottom-right',
		autoClose: 10000,
		closeOnClick: true,
		pauseOnHover: true,
		draggable: true,
	})

	return <ToastContainer theme={'colored'} />
}
