import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

type Props = {
	infoTekst: string
	onClose: () => void
}

export const InfoToast = ({ infoTekst, onClose }: Props) => {
	toast.info(infoTekst, {
		position: 'bottom-right',
		toastId: 'info-toast',
		autoClose: 10000,
		closeOnClick: true,
		pauseOnHover: true,
		draggable: true,
		onClose: () => onClose(),
	})

	return <ToastContainer theme={'light'} />
}
