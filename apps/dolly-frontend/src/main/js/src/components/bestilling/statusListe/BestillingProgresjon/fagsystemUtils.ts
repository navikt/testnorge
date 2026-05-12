const IN_PROGRESS_MESSAGES = [
	'Info',
	'INFO',
	'ADDING_TO_QUEUE',
	'RUNNING',
	'PENDING_COMPLETE',
	'Deployer',
	'Pågående',
]

const PINNED_IDS = new Set(['PDLIMPORT', 'PDL_FORVALTER', 'PDL_ORDRE', 'PDL_PERSONSTATUS'])

const IMPORT_HIDDEN_IDS = new Set(['PDL_FORVALTER', 'PDL_ORDRE', 'PDL_PERSONSTATUS'])

export const filterImportSubSteps = (statusList: any[]) => {
	const isImport = statusList.some((s) => s.id === 'PDLIMPORT')
	if (!isImport) return statusList
	return statusList.filter((s) => !IMPORT_HIDDEN_IDS.has(s.id))
}

export const sortFagsystemer = (list: any[]) => {
	const filtered = filterImportSubSteps(list)
	const pinned = filtered.filter((s) => PINNED_IDS.has(s.id))
	const rest = filtered.filter((s) => !PINNED_IDS.has(s.id))
	rest.sort((a, b) => (a.navn || '').localeCompare(b.navn || '', 'nb'))
	return [...pinned, ...rest]
}

export const calculateProgress = ({
	antallIdenter,
	antallLevert,
	erOrganisasjon,
	statusList,
	totalFagsystemer,
}: {
	antallIdenter: number
	antallLevert: number
	erOrganisasjon: boolean
	statusList: any[]
	totalFagsystemer: number
}) => {
	let percent: number
	let text: string

	if (!erOrganisasjon && antallIdenter === 1 && totalFagsystemer > 0) {
		const completedCount = statusList.filter((fagsystem) => {
			if (!fagsystem?.statuser?.length) return false
			return !fagsystem.statuser.some((s) =>
				IN_PROGRESS_MESSAGES.some((msg) => s?.melding?.includes(msg)),
			)
		}).length
		percent = totalFagsystemer > 0 ? (100 / totalFagsystemer) * completedCount : 0
		text = `${completedCount} av ${totalFagsystemer} steg fullført`
	} else {
		percent = antallIdenter > 0 ? (100 / antallIdenter) * antallLevert : 0
		text = `Oppretter ${antallLevert || 0} av ${antallIdenter}`
	}

	if (percent === 0) {
		percent = 10
	}

	return { percent, text }
}
