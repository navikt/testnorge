import React, { useEffect, useState } from 'react'
import { useBoolean } from 'react-use'

import './StatusPage.less'
import { Accordion } from '@navikt/ds-react'

import BlankHeader from '@/components/BlankHeader/BlankHeader'
import Loading from '@/components/loading/Loading'
import Icon from '@/components/icon/Icon'

export default () => {
	const [statuses, setStatuses] = useState({})
	const [dataLoading, setDataLoading] = useBoolean(true)

	useEffect(() => {
		const endpoint = 'https://dolly-backend.dev.intern.nav.no/v1/status'

		fetch(endpoint)
			.then((response) => response.json())
			.then((json) => {
				setStatuses(json)
				setDataLoading(false)
			})
			.catch((err) => {
				setDataLoading(false)
			})
	}, [])

	const serviceStatus = (service) => {
		if (service.alive === 'OK' && service.ready === 'OK') {
			return 'OK'
		}
		if (service.alive === 'OK') {
			return 'Warn'
		}
		return 'Feil'
	}

	const aggregateStatus = (services) => {
		if (!services) {
			return null
		}
		const statuses = services.map((service) => serviceStatus(service))

		const haveOk = statuses.includes('OK')
		const haveWarn = statuses.includes('Warn')
		const haveFeil = statuses.includes('Feil')

		if (!haveWarn && !haveFeil) {
			return 'OK'
		}
		if (!haveOk) {
			return 'Feil'
		}
		return 'Warn'
	}

	const iconType = (status) => {
		if (status === 'OK') {
			return 'feedback-check-circle'
		}
		if (status === 'Warn') {
			return 'report-problem-circle'
		}
		if (status === 'Feil') {
			return 'report-problem-triangle'
		}
		return 'arbeid'
	}

	const clientStatus = (consumer) => {
		const services = statuses[consumer]
		const serviceNames = Object.keys(services).map((name) => {
			return (
				<div className="consumer-service">
					<div className="consumer-service-name">
						<h5>{name}</h5> <span>({services[name].team})</span>
					</div>
					<div className="consumer-service-status">
						<Icon kind={iconType(serviceStatus(services[name]))} />
					</div>
				</div>
			)
		})
		const consumerStatus = aggregateStatus(Object.values(services))
		const cssConsumerClass = 'consumer-name ' + `consumer-${consumerStatus}`
		return (
			<div className="consumer-status" key={consumer}>
				<Accordion style={{ width: '100%' }}>
					<Accordion.Item>
						<Accordion.Header>
							<div className={cssConsumerClass}>
								<div>{consumer}</div>
								<div className="consumer-status-icon">
									<Icon kind={iconType(consumerStatus)} />
								</div>
							</div>
						</Accordion.Header>
						<Accordion.Content>
							<div>{serviceNames}</div>
						</Accordion.Content>
					</Accordion.Item>
				</Accordion>
			</div>
		)
	}

	const clients = Object.keys(statuses).map((name) => clientStatus(name))

	if (dataLoading) {
		return (
			<div
				style={{
					display: 'flex',
					width: '100%',
					height: '100vh',
					alignItems: 'center',
					justifyContent: 'center',
				}}
			>
				<Loading label="Sjekker tjenester" />
			</div>
		)
	}

	return (
		<>
			<BlankHeader />
			<div style={{ textAlign: 'center' }}>
				<h2>Dolly tjenestestatus</h2>
			</div>
			<div className="consumers-container">{clients}</div>
		</>
	)
}
