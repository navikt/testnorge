import './FinnPersonBestilling.less'
import Select, {
	components,
	DropdownIndicatorProps,
	GroupBase,
	InputActionMeta,
	OptionProps,
} from 'react-select'
import { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Highlighter from 'react-highlight-words'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { resetFeilmelding } from '@/ducks/finnPerson'
import { useReduxDispatch } from '@/utils/hooks/useRedux'
import { useSearchHotkey } from '@/utils/hooks/useSearchHotkey'
import { GroupedOption, NavigeringOption, SoekTypeValg } from './NavigeringTypes'
import { PersonSearchResult, PersonSearchSources, usePersonSearch } from './FinnPerson'
import { useBestillingSearch } from './FinnBestilling'
import { useGruppeSearch } from './FinnGruppe'

interface NavigeringContextValue {
	fragment: string
	shortcutKey: string
}

const NavigeringContext = createContext<NavigeringContextValue>({
	fragment: '',
	shortcutKey: '',
})

const customAsyncSelectStyles = {
	control: (provided: any) => ({
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

const formatGroupLabel = (data: GroupedOption) => {
	const getIconKind = () => {
		if (data.label === 'Bestillinger') return 'bestilling'
		if (data.label === 'Grupper') return 'group'
		return 'person'
	}

	return (
		<div className={'group'}>
			<Icon className={'group-icon'} kind={getIconKind()} />
			<span className={'group-label'}>{data.label}</span>
			<span className={'group-badge'}>{data.options.length}</span>
		</div>
	)
}

const CustomOption = ({
	children,
	...props
}: OptionProps<NavigeringOption, false, GroupBase<NavigeringOption>>) => {
	const { fragment } = useContext(NavigeringContext)
	return (
		<components.Option {...props} className={'group-option'}>
			<span data-testid={TestComponentSelectors.BUTTON_NAVIGER_DOLLY}>
				<Highlighter
					textToHighlight={children as string}
					searchWords={fragment?.replaceAll('#', '').split(' ')}
					autoEscape={true}
					caseSensitive={false}
				/>
			</span>
		</components.Option>
	)
}

const DropdownIndicator = (
	props: DropdownIndicatorProps<NavigeringOption, false, GroupBase<NavigeringOption>>,
) => {
	const { shortcutKey } = useContext(NavigeringContext)
	return (
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

const selectComponents = {
	Option: CustomOption,
	DropdownIndicator,
}

export const MIN_SEARCH_LENGTH = 3

const emptyPersonResult: PersonSearchResult = {
	group: { label: 'Personer', options: [] },
	hasMorePdlf: false,
	hasMorePdl: false,
}

export const mergePersonOptions = (
	existing: NavigeringOption[],
	incoming: NavigeringOption[],
): NavigeringOption[] => {
	const options = new Map(existing.map((option) => [option.value, option]))
	incoming.forEach((option) => {
		const current = options.get(option.value)
		if (!current || (current.label.includes('UKJENT') && !option.label.includes('UKJENT'))) {
			options.set(option.value, option)
		}
	})
	return Array.from(options.values())
}

const Navigering = () => {
	const dispatch = useReduxDispatch()
	const searchInputRef = useRef(null)
	const shortcutKey = useSearchHotkey(searchInputRef)

	const [fragment, setFragment] = useState('')
	const [options, setOptions] = useState<GroupedOption[]>([])
	const [isLoading, setIsLoading] = useState(false)
	const [isLoadingMore, setIsLoadingMore] = useState(false)
	const [error, setError] = useState<string | null>(null)
	const requestIdRef = useRef(0)
	const nextPageRef = useRef(1)
	const seedRef = useRef(0)
	const hasMorePdlfRef = useRef(false)
	const hasMorePdlRef = useRef(false)
	const isLoadingMoreRef = useRef(false)

	const {
		search: searchPerson,
		handleSelect: selectPerson,
		feilmelding: personFeilmelding,
		resetError: resetPersonError,
	} = usePersonSearch()
	const { search: searchBestilling, handleSelect: selectBestilling } = useBestillingSearch()
	const { search: searchGruppe, handleSelect: selectGruppe } = useGruppeSearch()

	const contextValue = useMemo(() => ({ fragment, shortcutKey }), [fragment, shortcutKey])
	const normalizedFragment = fragment.trim()

	const startSearch = useCallback(
		(tekst: string) => {
			const requestId = ++requestIdRef.current
			nextPageRef.current = 1
			hasMorePdlfRef.current = false
			hasMorePdlRef.current = false
			isLoadingMoreRef.current = false
			setIsLoadingMore(false)

			if (tekst.length < MIN_SEARCH_LENGTH) {
				setOptions([])
				setIsLoading(false)
				return
			}

			const seed = Math.floor(Math.random() * 2_147_483_647)
			seedRef.current = seed
			setIsLoading(true)

			const fetchOptions = async () => {
				const [personResult, bestillingResult, gruppeResult] = await Promise.allSettled([
					searchPerson(tekst, 0, seed),
					searchBestilling(tekst),
					searchGruppe(tekst),
				])

				if (requestId !== requestIdRef.current) {
					return
				}

				const getGroupResult = (
					result: PromiseSettledResult<GroupedOption>,
					fallback: GroupedOption,
				): GroupedOption => {
					if (result.status === 'fulfilled') return result.value
					setError(result.reason?.message)
					return fallback
				}

				const personer =
					personResult.status === 'fulfilled' ? personResult.value : emptyPersonResult
				if (personResult.status === 'rejected') {
					setError(personResult.reason?.message)
				}

				hasMorePdlfRef.current = personer.hasMorePdlf
				hasMorePdlRef.current = personer.hasMorePdl
				setOptions([
					getGroupResult(bestillingResult, { label: 'Bestillinger', options: [] }),
					getGroupResult(gruppeResult, { label: 'Grupper', options: [] }),
					personer.group,
				])
				setIsLoading(false)
			}

			void fetchOptions()
		},
		[searchPerson, searchBestilling, searchGruppe],
	)

	const handleLoadMore = useCallback(async () => {
		if (
			normalizedFragment.length < MIN_SEARCH_LENGTH ||
			isLoadingMoreRef.current ||
			(!hasMorePdlfRef.current && !hasMorePdlRef.current)
		) {
			return
		}

		const requestId = requestIdRef.current
		const sources: PersonSearchSources = {
			pdlf: hasMorePdlfRef.current,
			pdl: hasMorePdlRef.current,
			pdlAktoer: false,
		}
		isLoadingMoreRef.current = true
		setIsLoadingMore(true)

		try {
			const personer = await searchPerson(
				normalizedFragment,
				nextPageRef.current,
				seedRef.current,
				sources,
			)

			if (requestId !== requestIdRef.current) {
				return
			}

			nextPageRef.current += 1
			hasMorePdlfRef.current = personer.hasMorePdlf
			hasMorePdlRef.current = personer.hasMorePdl
			setOptions((current) =>
				current.map((group) =>
					group.label === 'Personer'
						? {
								...group,
								options: mergePersonOptions(group.options, personer.group.options),
							}
						: group,
				),
			)
		} catch (searchError: unknown) {
			if (requestId === requestIdRef.current) {
				setError(searchError instanceof Error ? searchError.message : 'Personsøk feilet')
			}
		} finally {
			if (requestId === requestIdRef.current) {
				isLoadingMoreRef.current = false
				setIsLoadingMore(false)
			}
		}
	}, [normalizedFragment, searchPerson])

	const handleSearchSelect = useCallback(
		(option: NavigeringOption | null) => {
			dispatch(resetFeilmelding())
			if (!option?.value) return

			requestIdRef.current += 1
			nextPageRef.current = 1
			hasMorePdlfRef.current = false
			hasMorePdlRef.current = false
			isLoadingMoreRef.current = false
			setFragment('')
			setOptions([])
			setIsLoading(false)
			setIsLoadingMore(false)
			setError(null)
			resetPersonError()

			switch (option.type) {
				case SoekTypeValg.PERSON:
					selectPerson(option.value)
					break
				case SoekTypeValg.BESTILLING:
					selectBestilling(option.value)
					break
				case SoekTypeValg.GRUPPE:
					selectGruppe(option.value)
					break
			}
		},
		[dispatch, resetPersonError, selectPerson, selectBestilling, selectGruppe],
	)

	const handleInputChange = useCallback(
		(tekst: string, actionMeta: InputActionMeta) => {
			if (actionMeta.action !== 'input-change') {
				return
			}
			dispatch(resetFeilmelding())
			resetPersonError()
			setError(null)
			setFragment(tekst)
			startSearch(tekst.trim())
		},
		[dispatch, resetPersonError, startSearch],
	)

	const displayError = error || personFeilmelding
	const windowHeight = window.innerHeight

	return (
		<ErrorBoundary>
			<NavigeringContext.Provider value={contextValue}>
				<div>
					<div
						data-testid={TestComponentSelectors.CONTAINER_FINN_PERSON_BESTILLING}
						className="finnperson-container skjemaelement"
					>
						<Select<NavigeringOption, false, GroupedOption>
							ref={searchInputRef}
							data-testid={TestComponentSelectors.SELECT_PERSON_SEARCH}
							classNamePrefix={'person-search'}
							styles={customAsyncSelectStyles}
							inputValue={fragment}
							options={options}
							isLoading={isLoading || isLoadingMore}
							onInputChange={handleInputChange}
							onMenuScrollToBottom={handleLoadMore}
							components={selectComponents}
							isClearable={true}
							value={null}
							formatGroupLabel={formatGroupLabel}
							maxMenuHeight={
								windowHeight > 800 ? 500 : windowHeight < 500 ? 300 : windowHeight - 400
							}
							onChange={handleSearchSelect}
							backspaceRemovesValue={true}
							aria-label="Person"
							placeholder={'Søk etter navn, ident, aktør-ID, bestilling eller gruppe'}
							noOptionsMessage={() =>
								normalizedFragment.length < MIN_SEARCH_LENGTH ? 'Skriv minst 3 tegn' : 'Ingen treff'
							}
						/>
					</div>
					{displayError && (
						<div
							data-testid={TestComponentSelectors.ERROR_MESSAGE_NAVIGERING}
							className="error-message"
							style={{ marginTop: '10px', maxWidth: '330px' }}
						>
							{displayError}
						</div>
					)}
				</div>
			</NavigeringContext.Provider>
		</ErrorBoundary>
	)
}
export default Navigering
