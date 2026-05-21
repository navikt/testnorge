const IN_PROGRESS_MESSAGES = [
    'Info',
    'INFO',
    'ADDING_TO_QUEUE',
    'RUNNING',
    'PENDING_COMPLETE',
    'Deployer',
    'Pågående',
]

const PRIORITY_ORDER: Record<string, number> = {
    PDL_FORVALTER: 0,
    PDLIMPORT: 0,
    PDL_ORDRE: 1,
    PDL_PERSONSTATUS: 2,
}

export const sortFagsystemer = (list: any[]) => {
    return [...list].sort((a, b) => {
        const priorityA = PRIORITY_ORDER[a.id] ?? Number.MAX_SAFE_INTEGER
        const priorityB = PRIORITY_ORDER[b.id] ?? Number.MAX_SAFE_INTEGER
        if (priorityA !== priorityB) return priorityA - priorityB
        return (a.navn || '').localeCompare(b.navn || '', 'nb')
    })
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
            return !fagsystem.statuser.some((s: any) =>
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

    return {percent, text}
}

const IMPORT_HIDDEN_IDS = new Set(['PDL_FORVALTER', 'PDL_ORDRE', 'PDL_PERSONSTATUS'])


export const filterImportSubSteps = (statusList: any[]) => {
    const isImport = statusList.some((s) => s.id === 'PDLIMPORT')
    if (!isImport) return statusList
    return statusList.filter((s) => !IMPORT_HIDDEN_IDS.has(s.id))
}