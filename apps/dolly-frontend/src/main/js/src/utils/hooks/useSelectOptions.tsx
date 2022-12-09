import useSWR from 'swr'
import { multiFetcherAll, multiFetcherBatchData } from '~/api'

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

const tpsforvalterUrl = '/tps-forvalteren-proxy/api/v1/dolly/testdata/hentpersoner'

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
		error: error,
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
			relasjoner.push(relasjon.relatertPersonsIdent)
		)
		person.fullmakt?.forEach((relasjon) => relasjoner.push(relasjon.motpartsPersonident))
		person.kontaktinformasjonForDoedsbo?.forEach((relasjon) =>
			relasjoner.push(relasjon.personSomKontakt?.identifikasjonsnummer)
		)
		person.sivilstand?.forEach(
			(relasjon) =>
				relasjon.relatertVedSivilstand && relasjoner.push(relasjon.relatertVedSivilstand)
		)
		person.vergemaalEllerFremtidsfullmakt?.forEach((relasjon) =>
			relasjoner.push(relasjon.vergeEllerFullmektig?.motpartsPersonident)
		)
		return relasjoner
	}

	const personData = []
	const dataFlat = data?.flatMap((i) => i.data?.hentPersonBolk)
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
		error: error,
	}
}

export const useTpsOptions = (gruppe) => {
	const maxAntall = 40
	let identListe = []
	for (let i = 0; i < gruppe?.length; i += maxAntall) {
		const listeDel = gruppe?.slice(i, i + maxAntall)
		identListe.push(listeDel.map((p) => p.ident))
	}

	const { data, error } = useSWR<any, Error>([tpsforvalterUrl, identListe], multiFetcherBatchData)

	const personData = []
	data?.flat().forEach((id) => {
		const mellomnavn = id?.mellomnavn ? `${id?.mellomnavn?.charAt(0)}.` : ''
		personData.push({
			value: id?.ident,
			label: `${id?.ident} - ${id?.fornavn} ${mellomnavn} ${id?.etternavn}`,
			relasjoner: id?.relasjoner?.map((r: any) => r?.personRelasjonMed?.ident),
		})
	})

	return {
		data: personData,
		loading: false,
		error: error,
	}
}
