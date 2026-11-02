import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { getTypePerson } from '@/utils/DataFormatter'

export const harFlereGjeldendeIdenttyper = (opts: BestillingsveilederContextType | undefined) =>
	!!opts?.is?.leggTilPaaGruppe || !!opts?.is?.opprettFraIdenter

export const getStandardIdenttype = (
	opts: BestillingsveilederContextType | undefined,
): string | null => (harFlereGjeldendeIdenttyper(opts) ? null : (opts?.identtype ?? null))

interface KonverterValg {
	visSyntetisk: boolean
	visId2032: boolean
}

export const getKonverterValg = (
	opts: BestillingsveilederContextType | undefined,
	eksisterendePerson: boolean,
): KonverterValg => {
	if (eksisterendePerson) {
		return { visSyntetisk: false, visId2032: false }
	}

	if (harFlereGjeldendeIdenttyper(opts)) {
		return { visSyntetisk: true, visId2032: true }
	}

	if (opts?.is?.leggTil) {
		const personFoerLeggTil = opts.personFoerLeggTil
		const ident = personFoerLeggTil?.pdlforvalter?.person?.ident ?? personFoerLeggTil?.pdl?.ident

		return {
			visSyntetisk: !!ident && getTypePerson(ident) === 'Standard',
			visId2032: !personFoerLeggTil?.pdlforvalter?.person?.id2032,
		}
	}

	return { visSyntetisk: false, visId2032: !opts?.id2032 }
}

const getMasterForIdenttype = (identtype: string | null | undefined) =>
	identtype === 'NPID' ? 'PDL' : 'FREG'

export const oppdaterNyidentForIdenttype = (
	nyident: NyIdent[],
	forrigeIdenttype: string | null | undefined,
	nyIdenttype: string,
): NyIdent[] => {
	const forrigeMaster = getMasterForIdenttype(forrigeIdenttype)
	const nyMaster = getMasterForIdenttype(nyIdenttype)

	return nyident.map((ident) => ({
		...ident,
		...(!ident.eksisterendeIdent &&
			ident.identtype === forrigeIdenttype && { identtype: nyIdenttype }),
		...((!ident.master || ident.master === forrigeMaster) && { master: nyMaster }),
	}))
}
