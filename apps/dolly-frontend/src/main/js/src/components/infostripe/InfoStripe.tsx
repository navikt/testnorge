import React, { useEffect, useState } from 'react'
import useSWR from 'swr'
import { fetcher } from '@/api'

import { DollyInfoAlert } from '@/components/infostripe/DollyInfoAlert'
import Button from '@/components/ui/button/Button'
import Icon from '@/components/ui/icon/Icon'

import './InfoStripe.less'
import * as _ from 'lodash-es'

const getInfoStripeUrl = `/dolly-backend/api/v1/infostripe`
const HIDDEN_ALERTS = 'HIDDEN_ALERTS'

const loadHiddenAlerts = (): string[] => {
	const json = sessionStorage.getItem(HIDDEN_ALERTS)
	if (json) {
		return JSON.parse(json)
	}
	return []
}

const saveHiddenAlerts = (alerts: string[]) => {
	sessionStorage.setItem(HIDDEN_ALERTS, JSON.stringify(alerts))
}

type InfoStripeType = {
	id: number
	message: string
	type: 'info' | 'warning' | 'error' | 'success'
	start: Date
	expires: Date
}

export const InfoStripe = () => {
	const [alerts, setAlerts] = useState([])
	const [showAll, setShowAll] = useState(false)
	const [visibleAlerts, setVisibleAlerts] = useState([])
	const [hiddenAlerts, setHiddenAlerts] = useState(loadHiddenAlerts())

	const hideAlert = (id) => {
		const newHiddenAlerts = hiddenAlerts
		newHiddenAlerts.push(id)
		setHiddenAlerts(newHiddenAlerts)
		saveHiddenAlerts(newHiddenAlerts)

		const activeAlerts = getVisibleAlerts(alerts)
		setVisibleAlerts(activeAlerts)
	}

	const hideAllAlerts = () => {
		const hideAlle = alerts.map((alert) => alert.id).filter((id) => !hiddenAlerts.includes(id))
		const allHidden = hiddenAlerts.concat(hideAlle)
		setHiddenAlerts(allHidden)
		saveHiddenAlerts(allHidden)
		setVisibleAlerts([])
	}

	const getVisibleAlerts = (alerts) => {
		if (!alerts || !_.isArray(alerts)) {
			return []
		}
		return alerts
			?.filter((alert) => !alert.hide)
			?.filter((alert) => !hiddenAlerts?.includes(alert.id))
	}

	const invertVisAlleMeldinger = () => {
		setShowAll(!showAll)
	}

	const { data } = useSWR<InfoStripeType, Error>(getInfoStripeUrl, fetcher, {
		refreshInterval: 60000,
		dedupingInterval: 60000,
	})

	useEffect(() => {
		const activeAlerts = getVisibleAlerts(data)
		setVisibleAlerts(activeAlerts)
		// @ts-ignore
		setAlerts(data)
	}, [data])

	if (!visibleAlerts) {
		return null
	}

	if (!visibleAlerts || !visibleAlerts.length) {
		return null
	}

	const expandButtonText = !showAll
		? `Vis alle meldinger ( ${visibleAlerts.length})`
		: 'Vis bare én melding'

	return (
		<div className="info-stripe">
			{!showAll && (
				<DollyInfoAlert
					type={visibleAlerts[0].type}
					text={visibleAlerts[0].message}
					key={visibleAlerts[0].id}
					id={visibleAlerts[0].id}
					onHide={hideAlert}
				/>
			)}
			{showAll &&
				visibleAlerts.map((alert) => {
					return (
						<DollyInfoAlert
							type={alert.type}
							text={alert.message}
							key={alert.id}
							id={alert.id}
							onHide={hideAlert}
						/>
					)
				})}
			{visibleAlerts.length > 1 && (
				<div className="alle-meldinger">
					<div className="expand-alle-meldinger">
						<Button
							onClick={invertVisAlleMeldinger}
							kind={showAll === false ? 'chevron-down' : 'chevron-up'}
						>
							{expandButtonText}
						</Button>
					</div>

					<div className="lukk-alle-meldinger">
						<Icon
							size={16}
							kind="kryss"
							onClick={hideAllAlerts}
							title="Lukk alle meldinger"
							style={{ cursor: 'pointer' }}
						/>
					</div>
				</div>
			)}
		</div>
	)
}
