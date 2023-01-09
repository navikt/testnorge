import './FinnPerson.less'
import { useAsyncFn } from 'react-use'
import AsyncSelect from 'react-select/async'
import { components } from 'react-select'
import { DollyApi, PdlforvalterApi, TpsfApi } from '@/service/Api'
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Icon from '@/components/ui/icon/Icon'
import { SoekTypeValg, VelgSoekTypeToggle } from '@/pages/gruppeOversikt/VelgSoekTypeToggle'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Highlighter from 'react-highlight-words'
import styled from 'styled-components'
import PersonSearch from '@/service/services/personsearch/PersonSearch'
import { Option } from '@/service/SelectOptionsOppslag'

type FinnPersonProps = {
	feilmelding: string
	gruppe: number
	resetFeilmelding: Function
	navigerTilPerson: Function
	navigerTilBestilling: Function
}

type Person = {
	ident: string
	fornavn: string
	mellomnavn?: string
	etternavn: string
	aktoerId?: string
}

type ResponsBestilling = {
	data: [
		{
			navn: string
			id: number
		}
	]
}

const StyledAsyncSelect = styled(AsyncSelect)`
	width: 80%;
	margin-top: 2px;
`

const FinnPersonBestilling = ({
	feilmelding,
	gruppe,
	resetFeilmelding,
	navigerTilPerson,
	navigerTilBestilling,
}: FinnPersonProps) => {
	const [soekType, setSoekType] = useState(SoekTypeValg.PERSON)
	const [searchQuery, setSearchQuery] = useState(null)
	const [fragment, setFragment] = useState('')
	const [error, setError] = useState(feilmelding)

	const [tpsfIdenter, setTpsfIdenter] = useState([])
	const [pdlfIdenter, setPdlfIdenter] = useState([])
	const [pdlIdenter, setPdlIdenter] = useState([])
	const [pdlAktoerer, setPdlAktoerer] = useState([])

	const customAsyncSelectStyles = {
		control: (provided: any, state: { isFocused: boolean }) => ({
			...provided,
			borderRadius: 0,
			borderWidth: 0,
			borderStyle: 'none',
			boxShadow: state.isFocused ? 'inset 0px 0px 2px 1px #5684ff' : null,
		}),
		indicatorSeparator: () => ({
			visibility: 'hidden',
		}),
	}

	const navigate = useNavigate()

	useEffect(() => {
		const feilmeldingIdent = feilmelding?.substring(0, 11)
		let finnesTpsf = false
		let finnesPdlf = false
		let finnesPdl = false

		if (feilmelding) {
			if (tpsfIdenter?.find((element) => element.ident === feilmeldingIdent)) {
				finnesTpsf = true
			}
			if (pdlfIdenter?.find((element) => element.ident === feilmeldingIdent)) {
				finnesPdlf = true
			}
			if (
				pdlIdenter?.find((element) => element.ident === feilmeldingIdent) ||
				pdlAktoerer?.find((element) => element.ident === feilmeldingIdent)
			) {
				finnesPdl = true
			}
		}

		let beskrivendeFeilmelding = feilmelding

		if (finnesTpsf || finnesPdlf || finnesPdl) {
			beskrivendeFeilmelding = `${feilmelding}. Personen er opprettet i et annet system med master:
			${finnesTpsf ? ' TPS' : ''}
			${finnesTpsf && finnesPdlf ? ', ' : ''}
			${finnesPdlf ? 'PDL' : ''}
			${(finnesTpsf || finnesPdlf) && finnesPdl ? ', ' : ''}
			${finnesPdl ? 'Test-Norge' : ''}
			, og eksisterer ikke i Dolly.`
		}

		setError(beskrivendeFeilmelding)
	}, [feilmelding])

	useEffect(() => {
		resetFeilmelding()
		if (!searchQuery) {
			return
		}
		soekType === SoekTypeValg.PERSON
			? navigerTilPerson(searchQuery)
			: navigerTilBestilling(searchQuery)
		return setSearchQuery(null)
	}, [searchQuery])

	useEffect(() => {
		if (fragment && !feilmelding) {
			resetFeilmelding()
		}
	}, [fragment])

	function mapToPersoner(personData: any, personer: Array<Option>) {
		if (!Array.isArray(personData)) {
			return
		}
		personData
			.filter((person: Person) => person.fornavn && person.etternavn)
			.forEach((person: Person) => {
				const navn = person.mellomnavn
					? `${person.fornavn} ${person.mellomnavn} ${person.etternavn}`
					: `${person.fornavn} ${person.etternavn}`
				personer.push({
					value: person.ident,
					label: `${person?.aktoerId || person.ident} - ${navn.toUpperCase()}`,
				})
			})
	}

	const soekBestillinger = async (tekst: string): Promise<Option[]> => {
		if (!tekst) {
			return []
		}
		return DollyApi.getBestillingerFragment(tekst).then((response: ResponsBestilling) => {
			if (!response?.data || response?.data?.length < 1) {
				return []
			}
			return response.data?.map((resp) => ({
				value: resp.id,
				label: `#${resp.id} - ${resp.navn}`,
			}))
		})
	}

	const soekPersoner = async (tekst: string): Promise<Option[]> => {
		if (!tekst) {
			return []
		}

		const [tpsfValues, pdlfValues, pdlValues, pdlAktoerValues] = (await Promise.allSettled([
			TpsfApi.soekPersoner(tekst),
			PdlforvalterApi.soekPersoner(tekst),
			PersonSearch.searchPdlFragment(tekst),
			DollyApi.getAktoerFraPdl(tekst),
		])) as any

		const personer: Array<Option> = []

		let pdlfPersoner = []
		if (pdlfValues?.status === 'fulfilled') {
			pdlfPersoner = pdlfValues.value?.data
			mapToPersoner(pdlfPersoner, personer)
			setPdlfIdenter(pdlfPersoner)
		} else {
			setError(pdlfValues?.reason?.message)
		}

		if (tpsfValues?.status === 'fulfilled') {
			let tpsfPersoner = tpsfValues.value?.data
			if (Array.isArray(tpsfPersoner) && Array.isArray(pdlfPersoner)) {
				tpsfPersoner = tpsfPersoner.filter(
					(person: Person) => !pdlfPersoner.map((p: Person) => p.ident).includes(person.ident)
				)
			}
			mapToPersoner(tpsfPersoner, personer)
			setTpsfIdenter(tpsfPersoner)
		} else {
			setError(tpsfValues?.reason?.message)
		}

		if (pdlValues?.status === 'fulfilled') {
			const pdlPersoner = pdlValues.value?.data
			mapToPersoner(pdlPersoner, personer)
			setPdlIdenter(pdlPersoner)
		} else {
			setError(pdlValues?.reason?.message)
		}
		if (pdlAktoerValues?.status === 'fulfilled') {
			const pdlAktoerer = [
				{
					ident: pdlAktoerValues.value?.data?.data?.hentIdenter?.identer?.[0]?.ident,
					aktoerId: pdlAktoerValues.value?.data?.data?.hentIdenter?.identer?.[1]?.ident,
					fornavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.fornavn,
					mellomnavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.mellomnavn,
					etternavn: pdlAktoerValues.value?.data?.data?.hentPerson?.navn?.[0]?.etternavn,
				},
			]
			mapToPersoner(pdlAktoerer, personer)
			setPdlAktoerer(pdlAktoerer)
		} else {
			setError(pdlAktoerValues?.reason?.message)
		}

		return personer
	}

	// @ts-ignore
	const [options, fetchOptions]: Promise<Option[]> = useAsyncFn(
		async (tekst) => {
			return soekType === SoekTypeValg.BESTILLING
				? soekBestillinger(tekst).catch((err: Error) => setError(err.message))
				: soekPersoner(tekst).catch((err: Error) => setError(err.message))
		},
		[soekType]
	)

	const handleChange = (tekst: string) => {
		fetchOptions(tekst)
		setFragment(tekst)
	}

	// @ts-ignore
	const CustomOption = ({ children, ...props }) => (
		// @ts-ignore
		<components.Option {...props}>
			<Highlighter
				textToHighlight={children}
				searchWords={fragment.split(' ')}
				autoEscape={true}
				caseSensitive={false}
			/>
		</components.Option>
	)

	const DropdownIndicator = (props: JSX.IntrinsicAttributes) => {
		return (
			// @ts-ignore
			<components.DropdownIndicator {...props}>
				<Icon kind={'search'} size={20} />
			</components.DropdownIndicator>
		)
	}

	useEffect(() => {
		if (gruppe && !window.location.pathname.includes(`/${gruppe}`)) {
			navigate(`/gruppe/${gruppe}`, { replace: true })
		}
	})

	return (
		<ErrorBoundary>
			<div>
				<div className="finnperson-container skjemaelement">
					<VelgSoekTypeToggle soekValg={soekType} setValgtSoekType={setSoekType} />
					{/*@ts-ignore*/}
					<StyledAsyncSelect
						defaultOptions={false}
						styles={customAsyncSelectStyles}
						loadOptions={fetchOptions}
						onInputChange={handleChange}
						components={{
							Option: CustomOption,
							DropdownIndicator,
						}}
						isClearable={true}
						options={options}
						value={null}
						onChange={(e: Option) => setSearchQuery(e?.value)}
						backspaceRemovesValue={true}
						label="Person"
						placeholder={
							soekType === SoekTypeValg.PERSON
								? 'Søk etter navn, ident eller aktør-ID'
								: 'Søk etter bestilling'
						}
						noOptionsMessage={() => 'Ingen treff'}
					/>
				</div>
				{error && (
					<div className="error-message" style={{ marginTop: '10px', maxWidth: '330px' }}>
						{error}
					</div>
				)}
			</div>
		</ErrorBoundary>
	)
}
export default FinnPersonBestilling
