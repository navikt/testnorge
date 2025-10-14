import { useEffect } from 'react'
import { toast } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

type Props = {
	applicationError: string | null
}

export const ErrorToast = ({ applicationError }: Props) => {
	useEffect(() => {
		if (!applicationError) return
		const feilmelding = applicationError.replace(/\?\S*/, '')
		toast.error(feilmelding, {
			position: 'bottom-right',
			autoClose: 5000,
			closeOnClick: true,
			pauseOnHover: true,
			draggable: true,
			containerId: 'global-toast',
		})
	}, [applicationError])
	return null
}
