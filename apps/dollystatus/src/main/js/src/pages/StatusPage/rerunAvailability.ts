import { FagsystemStatus } from '@/services/statusApi'

const COOLDOWN_MILLISECONDS = 5 * 60 * 1_000

export const formatTime = (value: string | null) =>
	value
		? new Intl.DateTimeFormat('nb-NO', {
				dateStyle: 'short',
				timeStyle: 'short',
			}).format(new Date(value))
		: 'Ikke kjørt'

export const rerunUnavailableReason = (
	statuses: FagsystemStatus[],
	anyRunActive: boolean,
	now: number,
) => {
	if (anyRunActive) {
		return 'Vent til den aktive testkjøringen er ferdig.'
	}
	const latestStartedAt = Math.max(
		0,
		...statuses.map((status) => (status.startedAt ? new Date(status.startedAt).getTime() : 0)),
	)
	const cooldownUntil = latestStartedAt + COOLDOWN_MILLISECONDS
	if (latestStartedAt > 0 && now < cooldownUntil) {
		return `Kan kjøres på nytt ${formatTime(new Date(cooldownUntil).toISOString())}.`
	}
	return null
}
