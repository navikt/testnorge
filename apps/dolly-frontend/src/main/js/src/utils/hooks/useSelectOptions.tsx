import useSWR from 'swr'
import { fetcher, multiFetcherAll } from '@/api'

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
	const maxAntall = 40
	const url = '/testnav-pdl-forvalter/api/v1/personer?'
	return sliceGruppe(gruppe, maxAntall, url)
}

const multiplePdlPersonUrl = (gruppe) => {
	if (!gruppe) return null
	const maxAntall = 40
	const url = '/dolly-backend/api/v1/pdlperson/identer?'
	return sliceGruppe(gruppe, maxAntall, url)
}

export const usePdlOptions = (gruppe) => {
	const { data, error } = useSWR<any, Error>([multiplePdlforvalterUrl(gruppe)], multiFetcherAll)

	const personData = []
	data?.flat().forEach((id) => {
		const navn = id?.person?.navn?.[0]
		const fornavn = navn?.fornavn || ''
		const mellomnavn = navn?.mellomnavn ? `${navn?.mellomnavn?.charAt(0)}.` : ''
		const etternavn = navn?.etternavn || ''
		personData.push({
			value: id?.person?.ident,
			label: `${id?.person?.ident} - ${fornavn} ${mellomnavn} ${etternavn}`,
			relasjoner: id?.relasjoner?.map((r) => r?.relatertPerson?.ident),
		})
	})

	return {
		data: personData,
		loading: false,
		error: gruppe ? error : undefined,
	}
}

export const useTestnorgeOptions = (gruppe) => {
	const { data, error } = useSWR<any, Error>([multiplePdlPersonUrl(gruppe)], multiFetcherAll)

	const getRelatertePersoner = (person) => {
		if (!person) {
			return null
		}
		const relasjoner = [] as Array<string>
		person.forelderBarnRelasjon?.forEach((relasjon) =>
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
