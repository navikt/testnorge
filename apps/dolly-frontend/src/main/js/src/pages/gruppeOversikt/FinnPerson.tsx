import { useCallback, useMemo, useRef, useState } from 'react'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import { Option } from '@/service/SelectOptionsOppslag'
import { identerSearch } from '@/service/services/dollysearch/DollySearch'
import { useReduxDispatch, useReduxSelector } from '@/utils/hooks/useRedux'
import { navigerTilPerson } from '@/ducks/finnPerson'
import { GroupedOption, SoekTypeValg } from './NavigeringTypes'

type Person = {
	ident: string
	fornavn?: string
	mellomnavn?: string
	etternavn?: string
	aktoerId?: string
}

export const PERSON_SEARCH_PAGE_SIZE = 10

type PdlfPersonerState = {
	pdlfIdenter: Person[]
	pdlIdenter: Person[]
	pdlAktoerer: Person[]
}

export type PersonSearchSources = {
	pdlf: boolean
	pdl: boolean
	pdlAktoer: boolean
}

export type PersonSearchResult = {
	group: GroupedOption
	hasMorePdlf: boolean
	hasMorePdl: boolean
}

type PersonSearchPage = PdlfPersonerState & {
	personer: Option[]
	hasMorePdlf: boolean
	hasMorePdl: boolean
}

const DEFAULT_SEARCH_SOURCES: PersonSearchSources = {
	pdlf: true,
	pdl: true,
	pdlAktoer: true,
}

function mapToPersoner(personData: any, personer: Array<Option>) {
	if (!Array.isArray(personData)) {
		return
	}
	personData
		.filter((person: Person) => person && person.ident)
		.forEach((person: Person) => {
			const hasName = person.fornavn || person.etternavn
			const navn = hasName
				? [person.fornavn, person.mellomnavn, person.etternavn].filter(Boolean).join(' ')
				: 'UKJENT'

			personer.push({
				value: person.ident,
				label: `${person?.aktoerId || person.ident} - ${navn.toUpperCase()}`,
			})
		})
}

const mergePersonerByIdent = (existing: Person[], incoming: Person[]) => {
	const personer = new Map(existing.map((person) => [person.ident, person]))
	incoming.forEach((person) => personer.set(person.ident, person))
	return Array.from(personer.values())
}

const filtrerPersoner = (personer: Option[]) => {
	// Sjekk hvilke identer som blir returnert baade fra pdl og pdlf
	const duplicateValues = new Set(
		personer
			.filter((person) => personer.filter((p) => p.value === person.value).length > 1)
			.map((person) => person.value),
	)

	// Filtrer ut personer som returneres fra baade pdl og pdlf, og har mangelfulle data
	const filtrertePersoner = personer.filter(
		(person) => !(person.label?.includes('UKJENT') && duplicateValues.has(person.value)),
	)
	return Array.from(new Map(filtrertePersoner.map((person) => [person.value, person])).values())
}

export const soekPersoner = async (
	tekst: string,
	setError: (error: string) => void,
	setPdlfPersonerState: (state: PdlfPersonerState) => void,
	side: number,
	seed: number,
	sources: PersonSearchSources = DEFAULT_SEARCH_SOURCES,
): Promise<PersonSearchPage> => {
	if (!tekst || tekst.includes('#')) {
		return {
			personer: [],
			pdlfIdenter: [],
			pdlIdenter: [],
			pdlAktoerer: [],
			hasMorePdlf: false,
			hasMorePdl: false,
		}
	}

	const emptyResponse = Promise.resolve({ data: [] })
	const [pdlfValues, pdlValues, pdlAktoerValues] = (await Promise.allSettled([
		sources.pdlf
			? PdlforvalterApi.soekPersoner(tekst, side, PERSON_SEARCH_PAGE_SIZE)
			: emptyResponse,
		sources.pdl ? identerSearch(tekst, side, PERSON_SEARCH_PAGE_SIZE, seed) : emptyResponse,
		sources.pdlAktoer ? DollyApi.getAktoerFraPdl(tekst) : emptyResponse,
	])) as any

	const personer: Array<Option> = []

	let pdlfPersoner: Person[] = []
	let pdlIdenter: Person[] = []
	let pdlAktoerer: Person[] = []

	if (pdlfValues?.status === 'fulfilled') {
		pdlfPersoner = pdlfValues.value?.data ?? []
		mapToPersoner(pdlfPersoner, personer)
	} else {
		setError(pdlfValues?.reason?.message)
	}

	if (pdlValues?.status === 'fulfilled') {
		const pdlPersoner = pdlValues.value?.data ?? []
		mapToPersoner(pdlPersoner, personer)
		pdlIdenter = pdlPersoner
	} else {
		setError(pdlValues?.reason?.message)
	}
	if (pdlAktoerValues?.status === 'fulfilled') {
		const ident = pdlAktoerValues.value?.data?.data?.hentIdenter?.identer?.[0]?.ident
		if (ident) {
			pdlAktoerer = [
				{
					ident,
					aktoerId: pdlAktoerValues.value?.data?.data?.hentIdenter?.identer?.[1]?.ident,
					fornavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.fornavn,
					mellomnavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.mellomnavn,
					etternavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.etternavn,
				},
			]
		}
		mapToPersoner(pdlAktoerer, personer)
	} else {
		setError(pdlAktoerValues?.reason?.message)
	}

	setPdlfPersonerState({ pdlfIdenter: pdlfPersoner, pdlIdenter, pdlAktoerer })

	return {
		personer,
		pdlfIdenter: pdlfPersoner,
		pdlIdenter,
		pdlAktoerer,
		hasMorePdlf: sources.pdlf && pdlfPersoner.length === PERSON_SEARCH_PAGE_SIZE,
		hasMorePdl: sources.pdl && pdlIdenter.length === PERSON_SEARCH_PAGE_SIZE,
	}
}

export const extractFeilmelding = (
	feilmelding: string | null,
	pdlfIdenter: Person[],
	pdlIdenter: Person[],
	pdlAktoerer: Person[],
): string | null => {
	const feilmeldingIdent = feilmelding?.substring(0, 11)
	let finnesPdlf = false
	let finnesPdl = false

	if (feilmelding) {
		if (pdlfIdenter?.find((element: Person) => element.ident === feilmeldingIdent)) {
			finnesPdlf = true
		}
		if (
			pdlIdenter?.find((element: Person) => element.ident === feilmeldingIdent) ||
			pdlAktoerer?.find((element: Person) => element.ident === feilmeldingIdent)
		) {
			finnesPdl = true
		}
	}

	let beskrivendeFeilmelding = feilmelding

	if (finnesPdlf || finnesPdl) {
		beskrivendeFeilmelding = `${feilmelding}. Personen er opprettet i et annet system med master
   ${finnesPdlf ? ' PDL' : ''}${finnesPdlf && finnesPdl ? ' og' : ''}${
			finnesPdl ? ' Test-Norge' : ''
		}, og eksisterer ikke i Dolly.`
	}
	return beskrivendeFeilmelding
}

export const usePersonSearch = () => {
	const dispatch = useReduxDispatch()
	const reduxFeilmelding = useReduxSelector((state) => state.finnPerson.feilmelding)

	const [pdlfIdenter, setPdlfIdenter] = useState<Person[]>([])
	const [pdlIdenter, setPdlIdenter] = useState<Person[]>([])
	const [pdlAktoerer, setPdlAktoerer] = useState<Person[]>([])
	const [searchError, setSearchError] = useState<string | null>(null)
	const activeSearchRef = useRef(0)

	const feilmelding = useMemo(
		() => searchError || extractFeilmelding(reduxFeilmelding, pdlfIdenter, pdlIdenter, pdlAktoerer),
		[searchError, reduxFeilmelding, pdlfIdenter, pdlIdenter, pdlAktoerer],
	)

	const search = useCallback(
		async (
			tekst: string,
			side: number,
			seed: number,
			sources: PersonSearchSources = DEFAULT_SEARCH_SOURCES,
		): Promise<PersonSearchResult> => {
			const activeSearch = activeSearchRef.current
			if (side === 0) {
				setSearchError(null)
			}

			const result = await soekPersoner(
				tekst,
				(error) => {
					if (activeSearch === activeSearchRef.current) {
						setSearchError(error)
					}
				},
				(state) => {
					if (activeSearch !== activeSearchRef.current) {
						return
					}
					setPdlfIdenter((current) =>
						side === 0 ? state.pdlfIdenter : mergePersonerByIdent(current, state.pdlfIdenter),
					)
					setPdlIdenter((current) =>
						side === 0 ? state.pdlIdenter : mergePersonerByIdent(current, state.pdlIdenter),
					)
					setPdlAktoerer((current) =>
						side === 0 ? state.pdlAktoerer : mergePersonerByIdent(current, state.pdlAktoerer),
					)
				},
				side,
				seed,
				sources,
			)

			const filtrertePersoner = filtrerPersoner(result.personer)

			return {
				group: {
					label: 'Personer',
					options: filtrertePersoner.map((person) => ({
						value: person.value as string,
						label:
							person.label?.length > 39 ? `${person.label?.substring(0, 36)}...` : person.label,
						type: SoekTypeValg.PERSON,
					})),
				},
				hasMorePdlf: result.hasMorePdlf,
				hasMorePdl: result.hasMorePdl,
			}
		},
		[],
	)

	const handleSelect = useCallback(
		(value: string) => {
			dispatch(navigerTilPerson(value))
		},
		[dispatch],
	)

	const resetError = useCallback(() => {
		activeSearchRef.current += 1
		setSearchError(null)
		setPdlfIdenter([])
		setPdlIdenter([])
		setPdlAktoerer([])
	}, [])

	return { search, handleSelect, feilmelding, resetError }
}
