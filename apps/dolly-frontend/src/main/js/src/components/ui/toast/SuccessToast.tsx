import { useEffect } from 'react'
import { toast } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

type Props = {
	message: string | null
}

export const SuccessToast = ({ message }: Props) => {
	useEffect(() => {
		if (!message) return
		toast.success(message, {
			position: 'bottom-right',
			autoClose: 5000,
			closeOnClick: true,
			pauseOnHover: true,
			draggable: true,
			containerId: 'global-toast',
		})
	}, [message])
	return null
}
