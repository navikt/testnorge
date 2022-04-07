import React from 'react'
import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import styled from 'styled-components'

const StyledToastContainer = styled(ToastContainer)`
	.Toastify__toast {
		background: #ba3a26;
	}
`

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

	return <StyledToastContainer theme={'colored'} />
}
