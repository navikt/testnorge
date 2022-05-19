import './FinnPerson.less'
import { useAsyncFn } from 'react-use'
import AsyncSelect from 'react-select/async'
import { components } from 'react-select'
import { DollyApi, PdlforvalterApi, TpsfApi } from '~/service/Api'
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
import { Option } from '~/service/SelectOptionsOppslag'
import { SoekTypeValg, VelgSoekTypeToggle } from '~/pages/gruppeOversikt/VelgSoekTypeToggle'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Highlighter from 'react-highlight-words'
import styled from 'styled-components'
import PersonSearch from '~/service/services/personsearch/PersonSearch'

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
	width: 100%;
`

const FinnPersonBestilling = ({
	feilmelding,
	gruppe,
	resetFeilmelding,
	navigerTilPerson,
	navigerTilBestilling,
}: FinnPersonProps) => {
	const [soekType, setValgtSoekType] = useState(SoekTypeValg.PERSON)
	const [searchQuery, setSearchQuery] = useState(null)
	const [fragment, setFragment] = useState('')

	const navigate = useNavigate()

	useEffect(() => {
		if (!searchQuery) return null
		soekType === SoekTypeValg.PERSON
			? navigerTilPerson(searchQuery)
			: navigerTilBestilling(searchQuery)
		setSearchQuery(null)
	}, [searchQuery])

	function mapToPersoner(personList: any, personer: Array<Option>) {
		personList
			.filter((person: Person) => person.fornavn !== undefined && person.etternavn !== undefined)
			.map((person: Person) => {
				const navn = person.mellomnavn
					? `${person.fornavn} ${person.mellomnavn} ${person.etternavn}`
					: `${person.fornavn} ${person.etternavn}`
				personer.push({
					value: person.ident,
					label: `${person.ident} - ${navn.toUpperCase()}`,
				})
			})
	}

	const soekBestillinger = async (tekst: string): Promise<Option[]> => {
		return DollyApi.getBestillingerFragment(tekst).then((response: ResponsBestilling) => {
			if (response?.data?.length < 1) return []
			return response.data?.map((resp) => ({
				value: resp.id,
				label: `#${resp.id} - ${resp.navn}`,
			}))
		})
	}

	const soekPersoner = async (tekst: string): Promise<Option[]> => {
		const { data: tpsfIdenter }: any = await TpsfApi.soekPersoner(tekst)
		const { data: pdlfIdenter }: any = await PdlforvalterApi.soekPersoner(tekst)
		const { data: pdlIdenter }: any = await PersonSearch.searchPdlFragment(tekst)
		const personer: Array<Option> = []
		mapToPersoner(tpsfIdenter, personer)
		mapToPersoner(pdlfIdenter, personer)
		mapToPersoner(pdlIdenter, personer)
		return personer
	}

	// @ts-ignore
	const [options, fetchOptions]: Promise<Option[]> = useAsyncFn(
		async (tekst) =>
			soekType === SoekTypeValg.BESTILLING
				? await soekBestillinger(tekst)
				: await soekPersoner(tekst),
		[soekType]
	)

	const handleChange = (tekst: string) => {
		fetchOptions(tekst)
		setFragment(tekst)
		resetFeilmelding()
	}

	// @ts-ignore
	const Option = ({ children, ...props }) => (
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

	if (gruppe && !window.location.pathname.includes(`/${gruppe}`))
		navigate(`/gruppe/${gruppe}`, { replace: true })

	return (
		<ErrorBoundary>
			<div className="finnperson-container skjemaelement">
				<VelgSoekTypeToggle setValgtSoekType={setValgtSoekType} />
				<StyledAsyncSelect
					defaultOptions={false}
					loadOptions={fetchOptions}
					onInputChange={handleChange}
					components={{
						Option,
						// @ts-ignore
						DropdownIndicator,
						IndicatorSeparator: () => null,
					}}
					isClearable={true}
					options={options}
					onChange={(e: Option) => setSearchQuery(e?.value)}
					cacheOptions={true}
					label="Person"
					placeholder={
						soekType === SoekTypeValg.PERSON ? 'Søk etter navn eller ident' : 'Søk etter bestilling'
					}
				/>
			</div>
			{feilmelding && (
				<div className="error-message" style={{ marginTop: '10px' }}>
					{feilmelding}
				</div>
			)}
		</ErrorBoundary>
	)
}
export default FinnPersonBestilling
