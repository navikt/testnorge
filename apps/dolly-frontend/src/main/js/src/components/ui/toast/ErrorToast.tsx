import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

type Props = {
	applicationError: string
}

export const ErrorToast = ({ applicationError }: Props) => {
	const feilmelding = applicationError?.replace?.(/\?\S*/, '')

	toast.error(feilmelding, {
		position: 'bottom-right',
		autoClose: 10000,
		closeOnClick: true,
		pauseOnHover: true,
		draggable: true,
	})

	return <ToastContainer theme={'light'} />
}
