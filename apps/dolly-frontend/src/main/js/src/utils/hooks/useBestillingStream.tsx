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
		bestilling: sseData || initialData,
		isStreaming: isConnected && !isComplete,
		loading: !sseData && !initialData,
	}
}
