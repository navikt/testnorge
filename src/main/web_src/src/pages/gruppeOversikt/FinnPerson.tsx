import React, { useState } from 'react'
//@ts-ignore
import AsyncSelect from 'react-select/async'
import { useAsyncFn } from 'react-use'
import { Redirect } from 'react-router-dom'
import useBoolean from '~/utils/hooks/useBoolean'
import { TpsfApi } from '~/service/Api'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import DollyModal from '~/components/ui/modal/DollyModal'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { Søkeknapp } from 'nav-frontend-ikonknapper'
import { AsyncFn } from 'react-use/lib/useAsync'

type FinnPerson = {
	naviger: Function
}

type Option = {
	value: string
	label: string
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
		}
	}
}

export default function FinnPerson({ naviger }: FinnPerson) {
	const [isFinnModalOpen, openFinnModal, closeFinnModal] = useBoolean(false)
	const [redirectToGruppe, setRedirect] = useBoolean()

	const [ident, setIdent] = useState('')
	const [gruppe, setGruppe] = useState(null)
	const [feilmelding, setFeilmelding] = useState(null)

	const [options, fetchOptions]: AsyncFn<any> = useAsyncFn(async tekst => {
		const { data }: any = await TpsfApi.soekPersoner(tekst)
		const options: Array<Option> = []
		data.map((person: Person) => {
			const navn = person.mellomnavn
				? `${person.fornavn} ${person.mellomnavn} ${person.etternavn}`
				: `${person.fornavn} ${person.etternavn}`
			options.push({
				value: person.ident,
				label: `${person.ident} - ${navn.toUpperCase()}`
			})
		})
		return options
	}, [])

	const handleChange = (tekst: string) => {
		fetchOptions(tekst)
		setFeilmelding(null)
	}

	const handleClose = () => {
		setIdent(null)
		setGruppe(null)
		setFeilmelding(null)
		closeFinnModal()
	}

	const navigerTilPerson = () => {
		naviger(ident).then((response: Respons) => {
			if (response.value.data.error) {
				setFeilmelding(response.value.data.message)
			} else {
				setGruppe(response.value.data.gruppe.id)
				setRedirect()
			}
		})
	}

	if (redirectToGruppe) return <Redirect to={`/gruppe/${gruppe}`} />

	return (
		<>
			{/* @ts-ignore */}
			<Søkeknapp onClick={openFinnModal} style={{ marginTop: '10px' }} kompakt="">
				<span>Finn testperson</span>
			</Søkeknapp>

			<DollyModal isOpen={isFinnModalOpen} closeModal={handleClose} width="40%" overflow="visible">
				<h1>Søk etter testperson</h1>
				<p>
					Du kan søke på både FNR/DNR/BOST og navn, eller deler av disse. Du vil da få en liste av
					aktuelle personer. Velg en person for å bli tatt direkte til testdatagruppen personen
					finnes i.
				</p>
				{/* @ts-ignore */}
				<Label name="Testperson" label="Testperson">
					<AsyncSelect
						defaultOptions={false}
						loadOptions={fetchOptions}
						onInputChange={handleChange}
						options={options}
						onChange={(e: Option) => setIdent(e.value)}
						cacheOptions={true}
						label="Person"
						placeholder="Begynn å skrive inn FNR/DNR/BOST eller navn..."
					/>
				</Label>
				{feilmelding && (
					<div className="error-message" style={{ marginBottom: '10px' }}>
						{feilmelding}
					</div>
				)}
				<div className="flexbox--all-center" style={{ marginTop: '20px' }}>
					<NavButton type="standard" onClick={handleClose} style={{ marginRight: '10px' }}>
						Avbryt
					</NavButton>
					<NavButton type="hoved" onClick={navigerTilPerson}>
						Gå til person
					</NavButton>
				</div>
			</DollyModal>
		</>
	)
}
