import { useCallback, useMemo, useState } from 'react'
import { useEventSource } from '@/utils/hooks/useEventSource'
import { Bestilling, useBestillingById } from '@/utils/hooks/useBestilling'

const getStreamUrl = (bestillingId: string | number) =>
	`/dolly-backend/api/v1/bestilling/${bestillingId}/stream`

export const useBestillingStream = (
	bestillingId: string | number,
	erOrganisasjon = false,
	enabled = true,
) => {
	const [sseFailed, setSseFailed] = useState(false)

	const url = useMemo(() => {
		if (!bestillingId || erOrganisasjon || !enabled || sseFailed) return null
		return getStreamUrl(bestillingId)
	}, [bestillingId, erOrganisasjon, enabled, sseFailed])

	const handleError = useCallback(() => {
		setSseFailed(true)
	}, [])

	const { data: sseData, isConnected, isComplete } = useEventSource<Bestilling>(
		url,
		['progress', 'completed'],
		{ onError: handleError, completeOnEvent: 'completed' },
	)

	const pollingId = sseFailed ? bestillingId : 0
	const { bestilling: polledBestilling, loading: pollingLoading } = useBestillingById(
		pollingId,
		erOrganisasjon,
		sseFailed && enabled,
	)

	if (erOrganisasjon || !enabled) {
		return { bestilling: null, isStreaming: false, loading: false }
	}

	if (sseFailed) {
		return {
			bestilling: polledBestilling || null,
			isStreaming: false,
			loading: pollingLoading,
		}
	}

	return {
		bestilling: sseData,
		isStreaming: isConnected && !isComplete,
		loading: !sseData,
	}
}
