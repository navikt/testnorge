import { useCallback, useMemo, useRef, useState } from 'react'
import { useEventSource } from '@/utils/hooks/useEventSource'
import { Bestilling, useBestillingById } from '@/utils/hooks/useBestilling'
import { BestillingStatusGruppe } from '@/types/bestilling'

const getStreamUrl = (bestillingId: string | number) =>
	`/dolly-backend/api/v1/bestilling/${bestillingId}/stream`

const mergeStatus = (
	incoming: BestillingStatusGruppe[],
	accumulated: BestillingStatusGruppe[],
): BestillingStatusGruppe[] => {
	const incomingIds = new Set(incoming.map((s) => s.id))
	const missing = accumulated.filter((s) => !incomingIds.has(s.id))
	return missing.length > 0 ? [...incoming, ...missing] : incoming
}

export const useBestillingStream = (
	bestillingId: string | number,
	erOrganisasjon = false,
	enabled = true,
) => {
	const [sseFailed, setSseFailed] = useState(false)
	const accumulatedStatusRef = useRef<BestillingStatusGruppe[]>([])

	const shouldFetchInitial = !!bestillingId && !erOrganisasjon && enabled
	const { bestilling: initialData, loading: initialLoading } = useBestillingById(
		shouldFetchInitial ? bestillingId : 0,
		erOrganisasjon,
		false,
	)

	const alreadyDone = initialData?.ferdig === true

	const url = useMemo(() => {
		if (!bestillingId || erOrganisasjon || !enabled || sseFailed || alreadyDone || initialLoading)
			return null
		return getStreamUrl(bestillingId)
	}, [bestillingId, erOrganisasjon, enabled, sseFailed, alreadyDone, initialLoading])

	const handleError = useCallback(() => {
		setSseFailed(true)
	}, [])

	const {
		data: sseData,
		isConnected,
		isComplete,
	} = useEventSource<Bestilling>(url, ['progress', 'completed'], {
		onError: handleError,
		completeOnEvent: 'completed',
		staleTimeoutMs: 10_000,
	})

	const mergedBestilling = useMemo(() => {
		const base = sseData || initialData
		if (!base?.status) return base
		if (base.ferdig) {
			accumulatedStatusRef.current = []
			return base
		}
		const merged = mergeStatus(base.status, accumulatedStatusRef.current)
		accumulatedStatusRef.current = merged
		return { ...base, status: merged }
	}, [sseData, initialData])

	const { bestilling: polledBestilling, loading: pollingLoading } = useBestillingById(
		sseFailed ? bestillingId : 0,
		erOrganisasjon,
		sseFailed && enabled,
	)

	if (erOrganisasjon || !enabled) {
		return { bestilling: null, isStreaming: false, loading: false }
	}

	if (alreadyDone) {
		return { bestilling: initialData, isStreaming: false, loading: false }
	}

	if (sseFailed) {
		return {
			bestilling: polledBestilling || null,
			isStreaming: false,
			loading: pollingLoading,
		}
	}

	return {
		bestilling: mergedBestilling || null,
		isStreaming: isConnected && !isComplete,
		loading: !sseData && !initialData,
	}
}
