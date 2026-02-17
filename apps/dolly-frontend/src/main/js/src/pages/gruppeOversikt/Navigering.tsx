import './FinnPersonBestilling.less'
import { components, DropdownIndicatorProps, GroupBase, OptionProps } from 'react-select'
import { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Highlighter from 'react-highlight-words'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { resetFeilmelding } from '@/ducks/finnPerson'
import { useReduxDispatch } from '@/utils/hooks/useRedux'
import AsyncSelect from 'react-select/async'
import { useSearchHotkey } from '@/utils/hooks/useSearchHotkey'
import { GroupedOption, NavigeringOption, SoekTypeValg } from './NavigeringTypes'
import { usePersonSearch } from './FinnPerson'
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

const Navigering = () => {
	const dispatch = useReduxDispatch()
	const searchInputRef = useRef(null)
	const shortcutKey = useSearchHotkey(searchInputRef)

	const [fragment, setFragment] = useState('')
	const [error, setError] = useState<string | null>(null)

	const {
		search: searchPerson,
		handleSelect: selectPerson,
		feilmelding: personFeilmelding,
		resetError: resetPersonError,
	} = usePersonSearch()
	const { search: searchBestilling, handleSelect: selectBestilling } = useBestillingSearch()
	const { search: searchGruppe, handleSelect: selectGruppe } = useGruppeSearch()

	const contextValue = useMemo(() => ({ fragment, shortcutKey }), [fragment, shortcutKey])

	const fetchOptions = useCallback(
		async (tekst: string): Promise<GroupedOption[]> => {
			const [personResult, bestillingResult, gruppeResult] = await Promise.allSettled([
				searchPerson(tekst),
				searchBestilling(tekst),
				searchGruppe(tekst),
			])

			const getResult = (
				result: PromiseSettledResult<GroupedOption>,
				fallback: GroupedOption,
			): GroupedOption => {
				if (result.status === 'fulfilled') return result.value
				setError(result.reason?.message)
				return fallback
			}

			return [
				getResult(personResult, { label: 'Personer', options: [] }),
				getResult(bestillingResult, { label: 'Bestillinger', options: [] }),
				getResult(gruppeResult, { label: 'Grupper', options: [] }),
			]
		},
		[searchPerson, searchBestilling, searchGruppe],
	)

	const handleSearchSelect = useCallback(
		(option: NavigeringOption | null) => {
			dispatch(resetFeilmelding())
			if (!option?.value) return

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
		[dispatch, selectPerson, selectBestilling, selectGruppe],
	)

	const handleInputChange = useCallback(
		(tekst: string) => {
			dispatch(resetFeilmelding())
			resetPersonError()
			setError(null)
			setFragment(tekst)
		},
		[dispatch, resetPersonError],
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
						<AsyncSelect
							ref={searchInputRef}
							data-testid={TestComponentSelectors.SELECT_PERSON_SEARCH}
							classNamePrefix={'person-search'}
							styles={customAsyncSelectStyles}
							loadOptions={fetchOptions}
							onInputChange={handleInputChange}
							components={selectComponents}
							isClearable={true}
							value={null}
							formatGroupLabel={formatGroupLabel}
							maxMenuHeight={
								windowHeight > 800 ? 500 : windowHeight < 500 ? 300 : windowHeight - 400
							}
							onChange={handleSearchSelect}
							backspaceRemovesValue={true}
							label="Person"
							placeholder={'Søk etter navn, ident, aktør-ID, bestilling eller gruppe'}
							noOptionsMessage={() => 'Ingen treff'}
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
