import useSWR from 'swr'
import { fetcher, multiFetcherAll } from '@/api'
import { getAlder } from '@/ducks/fagsystem'

const sliceGruppe = (gruppe, maxAntall, url) => {
	let urlListe = []
	for (let i = 0; i < gruppe.length; i += maxAntall) {
		const listeDel = gruppe.slice(i, i + maxAntall)
		urlListe.push(`${url}identer=${listeDel.map((p) => p.ident)}`)
	}
	return urlListe
}

const multiplePdlforvalterUrl = (gruppe) => {
	if (!gruppe) return null
	const maxAntall = 150
	const url = '/testnav-pdl-forvalter/api/v1/personer?'
	return sliceGruppe(gruppe, maxAntall, url)
}

const multiplePdlPersonUrl = (gruppe) => {
	if (!gruppe) return null
	const maxAntall = 150
	const url = '/person-service/api/v2/personer/identer?'
	return sliceGruppe(gruppe, maxAntall, url)
}

export const usePdlOptions = (gruppe, master = 'PDLF', visDetaljertLabel = false) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		master === 'PDLF' ? multiplePdlforvalterUrl(gruppe) : multiplePdlPersonUrl(gruppe),
		multiFetcherAll,
	)

	let payload = []
	if (master === 'PDLF') {
		payload = data
	} else {
		data?.forEach((element) => {
			payload.push(element?.data?.hentPersonBolk)
		})
	}

	const personData = []
	payload?.flat().forEach((id) => {
		const navn = id?.person?.navn?.[0]
		const fornavn = navn?.fornavn || ''
		const mellomnavn = navn?.mellomnavn ? `${navn?.mellomnavn?.charAt(0)}.` : ''
		const etternavn = navn?.etternavn || ''
		const ident = id?.person?.ident || id?.ident
		const foreldre =
			master === 'PDLF'
				? id.relasjoner
						?.filter((relasjon) => relasjon.relasjonType === 'FAMILIERELASJON_FORELDER')
						?.map((relasjon) => relasjon.relatertPerson?.ident)
				: id.person?.forelderBarnRelasjon
						?.filter((relasjon) => relasjon.minRolleForPerson === 'BARN')
						?.map((relasjon) => relasjon.relatertPersonsIdent)
		const foreldreansvar = (master = 'PDLF'
			? id.relasjoner
					?.filter((relasjon) => relasjon.relasjonType === 'FORELDREANSVAR_BARN')
					?.map((relasjon) => relasjon.relatertPerson?.ident)
			: null)
		const alder = getAlder(
			id.person?.foedselsdato?.[0]?.foedselsdato || id.person?.foedsel?.[0]?.foedselsdato,
		)
		const kjoenn = id?.person?.kjoenn?.[0].kjoenn?.toLowerCase()
		const label = `${ident} - ${fornavn} ${mellomnavn} ${etternavn}`
		personData.push({
			value: ident,
			label: visDetaljertLabel ? `${label} (${kjoenn} ${alder})` : label,
			relasjoner: id?.relasjoner?.map((r) => r?.relatertPerson?.ident),
			alder: alder,
			kjoenn: kjoenn,
			sivilstand: id.person?.sivilstand?.[0]?.type,
			vergemaal: id.person?.vergemaal?.length > 0,
			doedsfall: id.person?.doedsfall?.length > 0,
			foreldre: foreldre,
			foreldreansvar: foreldreansvar,
		})
	})

	return {
		data: personData,
		loading: isLoading,
		error: gruppe ? error : undefined,
	}
}

export const useTestnorgeOptions = (gruppe) => {
	const { data, error } = useSWR<any, Error>(multiplePdlPersonUrl(gruppe), multiFetcherAll)

	const getRelatertePersoner = (person) => {
		if (!person) {
			return null
		}
		const relasjoner = [] as Array<string>
		person?.forelderBarnRelasjon?.forEach((relasjon) =>
			relasjoner.push(relasjon.relatertPersonsIdent),
		)
		person.fullmakt?.forEach((relasjon) => relasjoner.push(relasjon.motpartsPersonident))
		person.kontaktinformasjonForDoedsbo?.forEach((relasjon) =>
			relasjoner.push(relasjon.personSomKontakt?.identifikasjonsnummer),
		)
		person.sivilstand?.forEach(
			(relasjon) =>
				relasjon.relatertVedSivilstand && relasjoner.push(relasjon.relatertVedSivilstand),
		)
		person.vergemaalEllerFremtidsfullmakt?.forEach((relasjon) =>
			relasjoner.push(relasjon.vergeEllerFullmektig?.motpartsPersonident),
		)
		return relasjoner
	}

	const personData = []
	const dataFlat = data?.flatMap((i) => i?.data?.hentPersonBolk)
	dataFlat?.forEach((id) => {
		const navn = id?.person?.navn?.[0]
		const mellomnavn = navn?.mellomnavn ? `${navn.mellomnavn.charAt(0)}.` : ''
		personData.push({
			value: id?.ident,
			label: `${id?.ident} - ${navn?.fornavn} ${mellomnavn} ${navn?.etternavn}`,
			relasjoner: getRelatertePersoner(id?.person),
		})
	})

	return {
		data: personData,
		loading: false,
		error: gruppe ? error : undefined,
	}
}

export const useHelsepersonellOptions = () => {
	function mapSamhandlerType(samhandlerType: string) {
		switch (samhandlerType) {
			case 'KI':
				return 'KIROPRAKTOR'
			case 'LE':
				return 'LEGE'
			case 'FT':
				return 'FYSIOTERAPEUT'
			case 'TL':
				return 'TANNLEGE'
			case 'MT':
				return 'MANUELLTERAPEUT'
			default:
				return samhandlerType
		}
	}

	const { data, isLoading, error } = useSWR<any, Error>(
		'/testnav-helsepersonell-service/api/v1/helsepersonell',
		fetcher,
	)

	const options = data?.helsepersonell?.map((helsepersonell) => ({
		value: helsepersonell.fnr,
		label: `${helsepersonell.fnr} - ${helsepersonell.fornavn} ${
			helsepersonell.mellomnavn ? helsepersonell.mellomnavn : ''
		} ${helsepersonell.etternavn} (${mapSamhandlerType(helsepersonell.samhandlerType)})`,
		fnr: helsepersonell.fnr,
		fornavn: helsepersonell.fornavn,
		mellomnavn: helsepersonell.mellomnavn,
		etternavn: helsepersonell.etternavn,
		hprId: helsepersonell.hprId,
		samhandlerType: mapSamhandlerType(helsepersonell.samhandlerType),
	}))
	return {
		helsepersonellOptions: options,
		loading: isLoading,
		helsepersonellError: error,
	}
}
