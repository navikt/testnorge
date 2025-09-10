import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { InfoStripeType, useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { DollyInfoAlert } from '@/components/infostripe/DollyInfoAlert'
import Button from '@/components/ui/button/Button'
import './InfoStripe.less'
import CloseButton from '@/components/ui/button/CloseButton/CloseButton'

const HIDDEN_ALERTS_KEY = 'HIDDEN_ALERTS'

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

	const visibleAlerts: InfoStripeType[] = useMemo(
		() => (alerts || []).filter((a) => !hiddenIds.includes(a.id)),
		[alerts, hiddenIds],
	)

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

	return (
		<div className="info-stripe">
			{!showAll && (
				<DollyInfoAlert
					type={visibleAlerts[0].type}
					text={visibleAlerts[0].message}
					id={visibleAlerts[0].id}
					onHide={hideAlert}
				/>
			)}

			{showAll &&
				visibleAlerts.map((alert) => (
					<DollyInfoAlert
						key={alert.id}
						type={alert.type}
						text={alert.message}
						id={alert.id}
						onHide={hideAlert}
					/>
				))}

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
