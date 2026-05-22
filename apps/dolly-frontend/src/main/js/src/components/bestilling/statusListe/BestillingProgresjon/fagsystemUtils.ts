const ERROR_KEYWORDS = ['feil', 'avvik', 'error', 'advarsel']

const isErrorMessage = (melding: string) =>
    ERROR_KEYWORDS.some((kw) => melding.toLowerCase().includes(kw))

const isCompletedStatus = (melding: string) =>
    melding === 'OK' || isErrorMessage(melding)

export const sortFagsystemer = (list: any[]) => {
    return [...list]
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
            return fagsystem.statuser.some((s: any) => isCompletedStatus(s?.melding || ''))
        }).length
        percent = totalFagsystemer > 0 ? (100 / totalFagsystemer) * completedCount : 0
        text = `${completedCount} av ${totalFagsystemer} steg fullført`
    } else {
        const displayLevert = Math.max(0, antallLevert - 1)
        percent = antallIdenter > 0 ? (100 / antallIdenter) * displayLevert : 0
        text = `Opprettet ${displayLevert} av ${antallIdenter}`
    }

    if (percent === 0) {
        percent = 10
    }

    return {percent, text}
}
