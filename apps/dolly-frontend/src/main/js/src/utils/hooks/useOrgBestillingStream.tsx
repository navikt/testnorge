import { useCallback, useMemo, useState } from 'react'
import { useEventSource } from '@/utils/hooks/useEventSource'
import { useOrganisasjonBestillingStatus } from '@/utils/hooks/useDollyOrganisasjoner'

interface Bestillingsstatus {
	id: number
	ferdig: boolean
	sistOppdatert: string
	antallLevert: number
	organisasjonNummer: string
	feil: string
	environments: string[]
	status: any[]
	bestilling: any
}

const getOrgStreamUrl = (bestillingId: string | number) =>
	`/dolly-backend/api/v1/organisasjon/bestilling/stream?bestillingId=${bestillingId}`

export const useOrgBestillingStream = (
	bestillingId: string | number,
	erOrganisasjon = false,
	enabled = true,
) => {
	const [sseFailed, setSseFailed] = useState(false)

	const url = useMemo(() => {
		if (!bestillingId || !erOrganisasjon || !enabled || sseFailed) return null
		return getOrgStreamUrl(bestillingId)
	}, [bestillingId, erOrganisasjon, enabled, sseFailed])

	const handleError = useCallback(() => {
		setSseFailed(true)
	}, [])

	const { data: sseData, isConnected, isComplete } = useEventSource<Bestillingsstatus>(
		url,
		['progress', 'completed'],
		{ onError: handleError, completeOnEvent: 'completed' },
	)

	const { bestillingStatus: polledStatus, loading: pollingLoading } =
		useOrganisasjonBestillingStatus(bestillingId, erOrganisasjon && sseFailed, sseFailed && enabled)

	if (!erOrganisasjon || !enabled) {
		return { bestillingStatus: null, isStreaming: false, loading: false }
	}

	if (sseFailed) {
		return {
			bestillingStatus: polledStatus || null,
			isStreaming: false,
			loading: pollingLoading,
		}
	}

	return {
		bestillingStatus: sseData,
		isStreaming: isConnected && !isComplete,
		loading: !sseData,
	}
}
