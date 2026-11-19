import React, { useCallback, useEffect, useState } from 'react'
import { GlobalAlert, VStack } from '@navikt/ds-react'
import { InfoStripeType, useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { getInfostripeStatus } from '@/components/infostripe/InfostripeIcon'
import Button from '@/components/ui/button/Button'
import './InfoStripe.less'
import CloseButton from '@/components/ui/button/CloseButton/CloseButton'

const HIDDEN_ALERTS_KEY = 'HIDDEN_ALERTS'
const INFOSTRIPE_TITLE = 'Melding fra Team Dolly'

const loadHidden = (): number[] => {
	try {
		const raw = sessionStorage.getItem(HIDDEN_ALERTS_KEY)
		if (!raw) return []
		const parsed = JSON.parse(raw)
		return Array.isArray(parsed) ? parsed : []
	} catch {
		return []
	}
}

const saveHidden = (ids: number[]) => {
	try {
		sessionStorage.setItem(HIDDEN_ALERTS_KEY, JSON.stringify(ids))
	} catch {
		console.warn('Kunne ikke lagre skjulte infostripe-meldinger i sessionStorage')
	}
}

export const InfoStripe: React.FC = () => {
	const { infostriper: alerts } = useDollyInfostriper()
	const [hiddenIds, setHiddenIds] = useState<number[]>(() => loadHidden())
	const [showAll, setShowAll] = useState(false)

	useEffect(() => {
		saveHidden(hiddenIds)
	}, [hiddenIds])

	const visibleAlerts: InfoStripeType[] = alerts.filter?.((a) => !hiddenIds.includes(a.id))

	const hideAlert = useCallback((id: number) => {
		setHiddenIds((prev) => (prev.includes(id) ? prev : [...prev, id]))
	}, [])

	const hideAll = useCallback(() => {
		setHiddenIds((prev) => {
			const allVisible = visibleAlerts.map((a) => a.id)
			const merged = new Set([...prev, ...allVisible])
			return Array.from(merged)
		})
	}, [visibleAlerts])

	const toggleShowAll = () => setShowAll((v) => !v)

	if (!visibleAlerts.length) return null

	const multiple = visibleAlerts.length > 1
	const alertsToShow = showAll ? visibleAlerts : visibleAlerts.slice(0, 1)

	return (
		<div className="info-stripe">
			<VStack gap="space-12" style={{ width: '100%' }}>
				{alertsToShow.map((alert) => (
					<GlobalAlert key={alert.id} status={getInfostripeStatus(alert.type)} size="small">
						<GlobalAlert.Header>
							<GlobalAlert.Title>{INFOSTRIPE_TITLE}</GlobalAlert.Title>
							<GlobalAlert.CloseButton onClick={() => hideAlert(alert.id)} />
						</GlobalAlert.Header>
						<GlobalAlert.Content>{alert.message}</GlobalAlert.Content>
					</GlobalAlert>
				))}
			</VStack>

			{multiple && (
				<div
					className="infostripe-controls"
					style={{ display: 'flex', alignItems: 'center', marginTop: '0.5rem' }}
				>
					<Button onClick={toggleShowAll} kind={showAll ? 'chevron-up' : 'chevron-down'}>
						{showAll ? 'Vis bare én melding' : `Vis alle meldinger (${visibleAlerts.length})`}
					</Button>
					<CloseButton onClick={hideAll} title="Lukk alle meldinger" style={{ marginTop: '5px' }} />
				</div>
			)}
		</div>
	)
}
