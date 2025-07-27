import './FinnPersonBestilling.less'
import { components } from 'react-select'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import React, { JSX, useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Highlighter from 'react-highlight-words'
import { Option } from '@/service/SelectOptionsOppslag'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { navigerTilBestilling, navigerTilPerson, resetFeilmelding } from '@/ducks/finnPerson'
import { useDispatch, useSelector } from 'react-redux'
import { identerSearch } from '@/service/services/dollysearch/DollySearch'
import AsyncSelect from 'react-select/async'
import { useSearchHotkey } from '@/utils/hooks/useSearchHotkey'

enum SoekTypeValg {
	PERSON = 'Person',
	BESTILLING = 'Bestilling',
}

const formatGroupLabel = (data: GroupedOption) => (
	<div className={'group'}>
		<Icon className={'group-icon'} kind={data.label === 'Bestillinger' ? 'bestilling' : 'person'} />
		<span className={'group-label'}>{data.label}</span>
		<span className={'group-badge'}>{data.options.length}</span>
	</div>
)

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
		},
	]
}

type FinnPersonBestillingOption = {
	value: string
	label: string
	type: SoekTypeValg
}

interface GroupedOption {
	label: string
	options: FinnPersonBestillingOption[]
}

const FinnPersonBestilling = () => {
	const dispatch = useDispatch()
	const searchInputRef = React.useRef(null)
	const shortcutKey = useSearchHotkey(searchInputRef)

	const feilmelding = useSelector((state: any) => state.finnPerson.feilmelding)
	const gruppe = useSelector((state: any) => state.finnPerson.navigerTilGruppe)

	const [searchQuery, setSearchQuery] = useState(null as unknown as FinnPersonBestillingOption)
	const [fragment, setFragment] = useState('')
	const [error, setError] = useState(feilmelding)

	const [pdlfIdenter, setPdlfIdenter] = useState([])
	const [pdlIdenter, setPdlIdenter] = useState([])
	const [pdlAktoerer, setPdlAktoerer] = useState([])

	const navigate = useNavigate()

	useEffect(() => {
		const beskrivendeFeilmelding = extractFeilmelding()
		setError(beskrivendeFeilmelding)
	}, [feilmelding])

	useEffect(() => {
		dispatch(resetFeilmelding())
		if (!searchQuery || !searchQuery.value) {
			return
		}
		searchQuery.type === SoekTypeValg.PERSON
			? dispatch(navigerTilPerson(searchQuery.value))
			: dispatch(navigerTilBestilling(searchQuery.value))
		return setSearchQuery(null)
	}, [searchQuery])

	useEffect(() => {
		if (gruppe && !window.location.pathname.includes(`/${gruppe}`)) {
			navigate(`/gruppe/${gruppe}`, { replace: true })
		}
	})

	const customAsyncSelectStyles = {
		control: (provided: any, state: { isFocused: boolean }) => ({
			...provided,
			minWidth: '360px',
		}),
		group: (provided: any) => ({
			...provided,
			paddingBottom: 0,
		}),
		menuList: (provided: any) => ({
			...provided,
			paddingBottom: 0,
		}),
		container: (provided: any) => ({
			...provided,
			width: '480px',
		}),
	}

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
		return DollyApi.getBestillingerFragment(tekst.replaceAll('#', '')).then(
			(response: ResponsBestilling) => {
				if (!response?.data || response?.data?.length < 1) {
					return []
				}
				return response.data?.map((resp) => ({
					value: resp.id,
					label: `#${resp.id} - ${resp.navn}`,
				}))
			},
		)
	}

	const extractFeilmelding = () => {
		const feilmeldingIdent = feilmelding?.substring(0, 11)
		let finnesPdlf = false
		let finnesPdl = false

		if (feilmelding) {
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

		if (finnesPdlf || finnesPdl) {
			beskrivendeFeilmelding = `${feilmelding}. Personen er opprettet i et annet system med master
   ${finnesPdlf ? ' PDL' : ''}${finnesPdlf && finnesPdl ? ' og' : ''}${
			finnesPdl ? ' Test-Norge' : ''
		}, og eksisterer ikke i Dolly.`
		}
		return beskrivendeFeilmelding
	}

	const soekPersoner = async (tekst: string): Promise<Option[]> => {
		if (!tekst || tekst.includes('#')) {
			return []
		}

		const [pdlfValues, pdlValues, pdlAktoerValues] = (await Promise.allSettled([
			PdlforvalterApi.soekPersoner(tekst),
			identerSearch(tekst),
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

	const fetchOptions = useCallback(async (tekst: string) => {
		const bestillinger = await soekBestillinger(tekst).catch((err: Error) => {
			setError(err.message)
			return undefined
		})

		const personer = await soekPersoner(tekst).catch((err: Error) => {
			setError(err.message)
			return undefined
		})

		return [
			{
				label: 'Personer',
				options: personer?.map((person) => ({
					value: person.value,
					label: person.label?.length > 39 ? `${person.label?.substring(0, 36)}...` : person.label,
					type: SoekTypeValg.PERSON,
				})),
			},
			{
				label: 'Bestillinger',
				options: bestillinger?.map((bestilling) => ({
					...bestilling,
					type: SoekTypeValg.BESTILLING,
				})),
			},
		]
	}, [])

	const handleChange = (tekst: string) => {
		dispatch(resetFeilmelding())
		setError('')
		setFragment(tekst)
	}

	const CustomOption = ({ children, ...props }) => (
		<components.Option {...props} className={'group-option'}>
			<span data-testid={TestComponentSelectors.BUTTON_NAVIGER_DOLLY}>
				<Highlighter
					textToHighlight={children}
					searchWords={fragment?.replaceAll('#', '').split(' ')}
					autoEscape={true}
					caseSensitive={false}
				/>
			</span>
		</components.Option>
	)

	const windowHeight = window.innerHeight

	const DropdownIndicator = (props: JSX.IntrinsicAttributes) => {
		return (
			// @ts-ignore
			<components.DropdownIndicator {...props}>
				<div
					style={{
						display: 'flex',
						alignItems: 'center',
						flexDirection: 'row',
						height: '24px',
					}}
				>
					<Icon fontSize={'1.5rem'} kind={'search'} />
					<p style={{ marginLeft: '5px' }}>{shortcutKey}</p>
				</div>
			</components.DropdownIndicator>
		)
	}

	return (
		<ErrorBoundary>
			<div>
				<div
					data-testid={TestComponentSelectors.CONTAINER_FINN_PERSON_BESTILLING}
					className="finnperson-container skjemaelement"
				>
					<AsyncSelect
						ref={searchInputRef}
						data-testid={TestComponentSelectors.SELECT_PERSON_SEARCH}
						classNamePrefix={'person-search'}
						styles={customAsyncSelectStyles}
						loadOptions={fetchOptions}
						onInputChange={handleChange}
						components={{
							Option: CustomOption,
							DropdownIndicator,
						}}
						isClearable={true}
						value={null}
						formatGroupLabel={formatGroupLabel}
						maxMenuHeight={
							windowHeight > 800 ? '500px' : `${windowHeight < 500 ? 300 : windowHeight - 400}px`
						}
						onChange={(e: GroupedOption) => setSearchQuery(e)}
						backspaceRemovesValue={true}
						label="Person"
						placeholder={'Søk etter navn, ident, aktør-ID, bestilling eller gruppe'}
						noOptionsMessage={() => 'Ingen treff'}
					/>
				</div>
				{error && (
					<div
						data-testid={TestComponentSelectors.ERROR_MESSAGE_NAVIGERING}
						className="error-message"
						style={{ marginTop: '10px', maxWidth: '330px' }}
					>
						{error}
					</div>
				)}
			</div>
		</ErrorBoundary>
	)
}
export default FinnPersonBestilling
