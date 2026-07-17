import { Sivilstand } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type PdlSortItem = {
	metadata?: {
		master?: string
		endringer?: Array<{ registrert: string }>
	}
	folkeregistermetadata?: {
		gyldighetstidspunkt?: string
	}
}

export const sortPdlItems = <T extends PdlSortItem>(items: T[]): T[] => {
	if (!items || items.length === 0) {
		return items
	}
	return [...items].sort((a, b) => {
		const masterA = a?.metadata?.master
		const masterB = b?.metadata?.master
		if (masterA !== masterB) {
			if (masterA === 'PDL') return -1
			if (masterB === 'PDL') return 1
		}
		const getTimestamp = (item: PdlSortItem): number => {
			if (item?.metadata?.master === 'PDL') {
				const registrertTimestamps =
					item?.metadata?.endringer
						?.map((endring) => new Date(endring.registrert).getTime())
						?.filter((timestamp) => !Number.isNaN(timestamp)) ?? []
				if (registrertTimestamps.length > 0) {
					return Math.max(...registrertTimestamps)
				}
			}
			const gyldighetstidspunkt = item?.folkeregistermetadata?.gyldighetstidspunkt
			const timestamp = gyldighetstidspunkt ? new Date(gyldighetstidspunkt).getTime() : 0
			return Number.isNaN(timestamp) ? 0 : timestamp
		}
		return getTimestamp(b) - getTimestamp(a)
	})
}

export const getSortedSivilstand = (sivilstander: Sivilstand[]) => {
	if (!sivilstander || sivilstander.length === 0) {
		return sivilstander
	}

	return [...sivilstander].sort(function (a: Sivilstand, b: Sivilstand) {
		if (a.gyldigFraOgMed < b.gyldigFraOgMed) {
			return 1
		}
		if (a.gyldigFraOgMed > b.gyldigFraOgMed) {
			return -1
		}
		return 0
	})
}
