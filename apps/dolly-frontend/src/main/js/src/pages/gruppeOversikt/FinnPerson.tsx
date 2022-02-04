import './FinnPerson.less'
// @ts-ignore
import { AsyncFn } from 'react-use/lib/useAsync'
import { useAsyncFn } from 'react-use'
// @ts-ignore
import AsyncSelect from 'react-select/async'
// @ts-ignore
import { components } from 'react-select'
import { PdlforvalterApi, TpsfApi } from '~/service/Api'
import useBoolean from '~/utils/hooks/useBoolean'
import React, { useState } from 'react'
import { Redirect } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
import { Option } from '~/service/SelectOptionsOppslag'

type FinnPersonProps = {
	naviger: Function
}

type Person = {
	ident: string
	fornavn: string
	mellomnavn?: string
	etternavn: string
}

type Respons = {
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

export default function FinnPerson({ naviger }: FinnPersonProps) {
	const [redirectToGruppe, setRedirect] = useBoolean()

	const [gruppe, setGruppe] = useState(null)
	const [feilmelding, setFeilmelding] = useState(null)

	function mapToPersoner(personList: any, personer: Array<Option>) {
		personList.map((person: Person) => {
			const navn = person.mellomnavn
				? `${person.fornavn} ${person.mellomnavn} ${person.etternavn}`
				: `${person.fornavn} ${person.etternavn}`
			personer.push({
				value: person.ident,
				label: `${person.ident} - ${navn.toUpperCase()}`,
			})
		})
	}

	const [options, fetchOptions]: AsyncFn<any> = useAsyncFn(async (tekst) => {
		const { data: tpsfIdenter }: any = await TpsfApi.soekPersoner(tekst)
		const { data: pdlIdenter }: any = await PdlforvalterApi.soekPersoner(tekst)
		const personer: Array<Option> = []
		mapToPersoner(tpsfIdenter, personer)
		mapToPersoner(pdlIdenter, personer)
		return personer
	}, [])

	const handleChange = (tekst: string) => {
		fetchOptions(tekst)
		setFeilmelding(null)
	}

	const navigerTilIdent = async (ident: string) => {
		naviger(ident).then((response: Respons) => {
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
			<components.DropdownIndicator {...props}>
				<Icon kind={'search'} size={20} />
			</components.DropdownIndicator>
		)
	}

	const redirectUrl = window.location.pathname.includes(`/${gruppe}/`)
		? `/gruppe/${gruppe}`
		: `/gruppe/${gruppe}/`
	if (redirectToGruppe) return <Redirect to={redirectUrl} />

	return (
		<div>
			<div className="finnperson-container skjemaelement">
				<AsyncSelect
					defaultOptions={false}
					loadOptions={fetchOptions}
					onInputChange={handleChange}
					components={{
						// @ts-ignore
						IndicatorSeparator() {
							return null
						},
						DropdownIndicator,
					}}
					isClearable={true}
					options={options}
					onChange={(e: Option) => (e ? navigerTilIdent(e.value) : null)}
					cacheOptions={true}
					label="Person"
					placeholder="Søk etter navn eller ident"
				/>
			</div>
			{feilmelding && (
				<div className="error-message" style={{ marginTop: '10px' }}>
					{feilmelding}
				</div>
			)}
		</div>
	)
}
