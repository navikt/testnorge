import React, { useEffect, useState } from 'react'

import './StatusPage.less'
import { Accordion, Heading } from '@navikt/ds-react'

import BlankHeader from '@/components/BlankHeader/BlankHeader'
import Loading from '@/components/loading/Loading'
import Icon from '@/components/icon/Icon'

type Service = {
	alive: string
	ready: string
	team: string
}

type Statuses = Record<string, Record<string, Service>>

type ServiceStatus = 'OK' | 'Warn' | 'Feil'

const statusLabels: Record<ServiceStatus, string> = {
	OK: 'OK',
	Warn: 'Varsel',
	Feil: 'Feil',
}

export default () => {
	const [statuses, setStatuses] = useState<Statuses>({})
	const [dataLoading, setDataLoading] = useState(true)

	useEffect(() => {
		const endpoint = 'https://dolly-backend.intern.dev.nav.no/internal/status'

		fetch(endpoint, {
			headers: {
				'Content-Type': 'application/json',
				'Access-Control-Allow-Origin': 'https://dolly-backend.intern.dev.nav.no',
			},
		})
			.then((response) => response.json())
			.then((json: Statuses) => {
				setStatuses(json)
				setDataLoading(false)
			})
			.catch(() => {
				setDataLoading(false)
			})
	}, [])

	const serviceStatus = (service: Service): ServiceStatus => {
		if (service.alive === 'OK' && service.ready === 'OK') {
			return 'OK'
		}
		if (service.alive === 'OK') {
			return 'Warn'
		}
		return 'Feil'
	}

	const aggregateStatus = (services: Service[]): ServiceStatus => {
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

	const iconType = (status: ServiceStatus) => {
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

	const clientStatus = (consumer: string, services: Record<string, Service>) => {
		const consumerStatus = aggregateStatus(Object.values(services))

		return (
			<Accordion.Item className="consumer-status" key={consumer}>
				<Accordion.Header className="consumer-header">
					<span className={`consumer-header-content consumer-${consumerStatus}`}>
						<span className="consumer-name">
							{consumer}
							<span className="consumer-status-assistive">
								{' '}
								har status {statusLabels[consumerStatus]}
							</span>
						</span>
						<span className="consumer-status-icon">
							<Icon kind={iconType(consumerStatus)} />
						</span>
					</span>
				</Accordion.Header>
				<Accordion.Content className="consumer-content">
					<div className="consumer-services">
						{Object.entries(services).map(([name, service]) => {
							const status = serviceStatus(service)

							return (
								<div className="consumer-service" key={name}>
									<div className="consumer-service-name">
										<span className="consumer-service-title">{name}</span>
										<span className="consumer-service-team">({service.team})</span>
										<span className="consumer-status-assistive">
											{' '}
											har status {statusLabels[status]}
										</span>
									</div>
									<div className="consumer-service-status">
										<Icon kind={iconType(status)} />
									</div>
								</div>
							)
						})}
					</div>
				</Accordion.Content>
			</Accordion.Item>
		)
	}

	const clients = Object.entries(statuses).map(([name, services]) => clientStatus(name, services))

	if (dataLoading) {
		return (
			<div className="status-page-loading">
				<Loading label="Sjekker tjenester" />
			</div>
		)
	}

	return (
		<>
			<BlankHeader />
			<div className="status-page">
				<Heading align="center" className="status-page-title" level="2" size="large">
					Dolly tjenestestatus
				</Heading>
				<Accordion className="consumers-accordion" indent={false}>
					{clients}
				</Accordion>
			</div>
		</>
	)
}
