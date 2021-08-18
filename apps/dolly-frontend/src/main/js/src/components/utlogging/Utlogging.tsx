import React, { useEffect, useState } from 'react'

import './Utlogging.less'
import DollyModal from '~/components/ui/modal/DollyModal'
import { Hovedknapp, Knapp } from 'nav-frontend-knapper'
import Api from '~/api'
import ProgressBar from 'fremdriftslinje'
import { WarningFilled } from '@navikt/ds-icons'

function getCookie(cookieName: string) {
	const name = cookieName + '='
	const decodedCookie = decodeURIComponent(document.cookie)
	const list = decodedCookie.split(';')
	for (let index = 0; index < list.length; index++) {
		let value = list[index]
		while (value.charAt(0) == ' ') {
			value = value.substring(1)
		}
		if (value.indexOf(name) == 0) {
			return value.substring(name.length, value.length)
		}
	}
	return ''
}

const SHOW_MODAL_WHEN_TIME_LEFT = 60 * 1000

const Utlogging = () => {
	const [serverTime, setServerTime] = useState(parseInt(getCookie('serverTime')))

	const calculateOffset = () => {
		const calcExpiry = parseInt(getCookie('sessionExpiry'))
		return calcExpiry - new Date().getTime() - serverTime + serverTime
	}

	const [milliseconds, setMilliseconds] = useState(calculateOffset())
	const [progress, setProgress] = useState(100)

	useEffect(() => {
		const calcServerTime = parseInt(getCookie('serverTime'))
		if (serverTime !== calcServerTime) {
			setServerTime(calcServerTime)
		}
		const value = (milliseconds / SHOW_MODAL_WHEN_TIME_LEFT) * 100
		setProgress(value)

		if (value > 0) {
			setTimeout(() => setMilliseconds(calculateOffset()), 1000)
		}
	}, [milliseconds])

	const logout = () => Api.fetch('/logout', { method: 'POST' }).then(() => location.reload())
	const continueSession = () => Api.fetch('/session/ping', { method: 'GET' })

	if (milliseconds <= 0) {
		location.reload()
	}

	return (
		<DollyModal
			minWidth={670}
			isOpen={milliseconds <= SHOW_MODAL_WHEN_TIME_LEFT}
			onRequestClose={logout}
			noCloseButton={true}
			contentLabel="Utlogging rute"
		>
			<div className="utlogging">
				<WarningFilled className="utlogging__ikon" />
				<h1 className="utlogging__title">
					Du har ikke vært aktiv på en stund, og vil snart bli logget ut av Dolly
				</h1>
				<ProgressBar
					now={milliseconds < 0 ? 0 : Math.round(progress)}
					status={milliseconds < 0 ? 'error' : 'inprogress'}
				>
					<p className="utlogging__progressbar__text">
						Gjenstående tid: {Math.round(milliseconds / 1000)}s
					</p>
				</ProgressBar>
				<div className="utlogging__button-group">
					<Knapp className="utlogging__button" onClick={logout}>
						Logg ut nå
					</Knapp>
					<Hovedknapp className="utlogging__button" onClick={continueSession}>
						Forbli innlogget
					</Hovedknapp>
				</div>
			</div>
		</DollyModal>
	)
}
export default Utlogging
