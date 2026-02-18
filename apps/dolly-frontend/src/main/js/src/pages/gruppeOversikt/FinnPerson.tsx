import { useCallback, useMemo, useState } from 'react'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import { Option } from '@/service/SelectOptionsOppslag'
import { identerSearch } from '@/service/services/dollysearch/DollySearch'
import { useReduxDispatch, useReduxSelector } from '@/utils/hooks/useRedux'
import { navigerTilPerson } from '@/ducks/finnPerson'
import { SoekTypeValg, GroupedOption } from './NavigeringTypes'

type Person = {
	ident: string
	fornavn: string
	mellomnavn?: string
	etternavn: string
	aktoerId?: string
}

type PdlfPersonerState = {
	pdlfIdenter: Person[]
	pdlIdenter: Person[]
	pdlAktoerer: Person[]
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

export const soekPersoner = async (
	tekst: string,
	setError: (error: string) => void,
	setPdlfPersonerState: (state: PdlfPersonerState) => void,
): Promise<Option[]> => {
	if (!tekst || tekst.includes('#')) {
		return []
	}

	const [pdlfValues, pdlValues, pdlAktoerValues] = (await Promise.allSettled([
		PdlforvalterApi.soekPersoner(tekst),
		identerSearch(tekst),
		DollyApi.getAktoerFraPdl(tekst),
	])) as any

	const personer: Array<Option> = []

	let pdlfPersoner: Person[] = []
	let pdlIdenter: Person[] = []
	let pdlAktoerer: Person[] = []

	if (pdlfValues?.status === 'fulfilled') {
		pdlfPersoner = pdlfValues.value?.data
		mapToPersoner(pdlfPersoner, personer)
	} else {
		setError(pdlfValues?.reason?.message)
	}

	if (pdlValues?.status === 'fulfilled') {
		const pdlPersoner = pdlValues.value?.data
		mapToPersoner(pdlPersoner, personer)
		pdlIdenter = pdlPersoner
	} else {
		setError(pdlValues?.reason?.message)
	}
	if (pdlAktoerValues?.status === 'fulfilled') {
		pdlAktoerer = [
			{
				ident: pdlAktoerValues.value?.data?.data?.hentIdenter?.identer?.[0]?.ident,
				aktoerId: pdlAktoerValues.value?.data?.data?.hentIdenter?.identer?.[1]?.ident,
				fornavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.fornavn,
				mellomnavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.mellomnavn,
				etternavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.etternavn,
			},
		]
		mapToPersoner(pdlAktoerer, personer)
	} else {
		setError(pdlAktoerValues?.reason?.message)
	}

	setPdlfPersonerState({ pdlfIdenter: pdlfPersoner, pdlIdenter, pdlAktoerer })

	return personer
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

	const feilmelding = useMemo(
		() => searchError || extractFeilmelding(reduxFeilmelding, pdlfIdenter, pdlIdenter, pdlAktoerer),
		[searchError, reduxFeilmelding, pdlfIdenter, pdlIdenter, pdlAktoerer],
	)

	const search = useCallback(async (tekst: string): Promise<GroupedOption> => {
		setSearchError(null)
		const personer = await soekPersoner(tekst, setSearchError, (state) => {
			setPdlfIdenter(state.pdlfIdenter)
			setPdlIdenter(state.pdlIdenter)
			setPdlAktoerer(state.pdlAktoerer)
		})
		return {
			label: 'Personer',
			options:
				personer?.map((person) => ({
					value: person.value as string,
					label:
						person.label?.length > 39 ? `${person.label?.substring(0, 36)}...` : person.label,
					type: SoekTypeValg.PERSON,
				})) ?? [],
		}
	}, [])

	const handleSelect = useCallback(
		(value: string) => {
			dispatch(navigerTilPerson(value))
		},
		[dispatch],
	)

	const resetError = useCallback(() => {
		setSearchError(null)
	}, [])

	return { search, handleSelect, feilmelding, resetError }
}
