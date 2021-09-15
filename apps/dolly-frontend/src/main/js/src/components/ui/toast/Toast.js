import React, { PureComponent } from 'react'
import { toast, ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

export default class Toaster extends PureComponent {
	componentDidMount() {
		const { error, clearErrors } = this.props
		if (error) {
			toast.error(error, {
				position: toast.POSITION.BOTTOM_RIGHT,
				onClose: clearErrors,
				autoClose: 10000,
			})
		}
	}

	render() {
		return <ToastContainer />
	}
}
