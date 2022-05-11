import './FinnPerson.less'
// @ts-ignore
import { AsyncFn } from 'react-use/lib/useAsync'
import { useAsyncFn } from 'react-use'
// @ts-ignore
import AsyncSelect from 'react-select/async'
// @ts-ignore
import { components } from 'react-select'
import { DollyApi, PdlforvalterApi, TpsfApi } from '~/service/Api'
import useBoolean from '~/utils/hooks/useBoolean'
import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
import { Option } from '~/service/SelectOptionsOppslag'
import { SoekTypeValg, VelgSoekTypeToggle } from '~/pages/gruppeOversikt/VelgSoekTypeToggle'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import styled from 'styled-components'
import PersonSearch from '~/service/services/personsearch/PersonSearch'

type FinnPersonProps = {
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

type ResponsNavigering = {
	value: {
		data: {
			gruppe?: {
				id: number
			}
			error?: string
			message?: string
			sidetall?: number
		}
	}
}

const StyledAsyncSelect = styled(AsyncSelect)`
	width: 100%;
`

export default function FinnPerson({ navigerTilPerson, navigerTilBestilling }: FinnPersonProps) {
	const [redirectToGruppe, setRedirect] = useBoolean()

	const [gruppe, setGruppe] = useState(null)
	const [feilmelding, setFeilmelding] = useState(null)
	const [soekType, setValgtSoekType] = useState(SoekTypeValg.PERSON)

	const navigate = useNavigate()

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

	const soekBestillinger = async (tekst: string) => {
		return DollyApi.getBestillingerFragment(tekst).then((response: ResponsBestilling) => {
			if (response?.data?.length < 1) return []
			return response.data?.map((resp) => ({
				value: resp.id,
				label: `#${resp.id} - ${resp.navn}`,
			}))
		})
	}

	const soekPersoner = async (tekst: string) => {
		const { data: tpsfIdenter }: any = await TpsfApi.soekPersoner(tekst)
		const { data: pdlfIdenter }: any = await PdlforvalterApi.soekPersoner(tekst)
		const { data: pdlIdenter }: any = await PersonSearch.searchPdlFragment(tekst)
		const personer: Array<Option> = []
		mapToPersoner(tpsfIdenter, personer)
		mapToPersoner(pdlfIdenter, personer)
		mapToPersoner(pdlIdenter, personer)
		return personer
	}

	const [options, fetchOptions]: AsyncFn<any> = useAsyncFn(
		async (tekst) => {
			return soekType === SoekTypeValg.BESTILLING
				? await soekBestillinger(tekst)
				: await soekPersoner(tekst)
		},
		[soekType]
	)

	const handleChange = (tekst: string) => {
		fetchOptions(tekst)
		setFeilmelding(null)
	}

	const navigerTilIdent = async (ident: string) => {
		navigerTilPerson(ident).then((response: ResponsNavigering) => {
			window.sessionStorage.setItem('sidetall', String(response.value.data.sidetall))
			if (response.value.data.error) {
				setFeilmelding(response.value.data.message)
			} else {
				setGruppe(response.value.data.gruppe.id)
				setRedirect()
			}
		})
	}

	const navigerTilBestillingId = async (bestillingId: string) => {
		navigerTilBestilling(bestillingId).then((response: ResponsNavigering) => {
			window.sessionStorage.setItem('sidetall', String(response.value.data.sidetall))
			if (response.value.data.error) {
				setFeilmelding(response.value.data.message)
			} else {
				setGruppe(response.value.data.gruppe.id)
				setRedirect()
			}
		})
	}

	const DropdownIndicator = (props: JSX.IntrinsicAttributes) => {
		return (
			// @ts-ignore
			<components.DropdownIndicator {...props}>
				<Icon kind={'search'} size={20} />
			</components.DropdownIndicator>
		)
	}

	if (redirectToGruppe && !window.location.pathname.includes(`/${gruppe}`))
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
						// @ts-ignore
						IndicatorSeparator() {
							return null
						},
						// @ts-ignore
						DropdownIndicator,
					}}
					isClearable={true}
					options={options}
					onChange={(e: Option) => {
						soekType === SoekTypeValg.PERSON
							? navigerTilIdent(e.value)
							: navigerTilBestillingId(e.value)
					}}
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
