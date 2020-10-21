import React, { useState } from 'react'
import AsyncSelect from 'react-select/async'
import { useAsyncFn } from 'react-use'
import { Redirect } from 'react-router-dom'
import useBoolean from '~/utils/hooks/useBoolean'
import { TpsfApi } from '~/service/Api'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import DollyModal from '~/components/ui/modal/DollyModal'
import { Label } from '~/components/ui/form/inputs/label/Label'

export default function FinnPerson({ naviger }) {
	const [isFinnModalOpen, openFinnModal, closeFinnModal] = useBoolean(false)
	const [soekTekst, setSoekTekst] = useState('')
	const [ident, setIdent] = useState(null)
	const [gruppe, setGruppe] = useState('')
	const [redirectToGruppe, setRedirect] = useBoolean()
	const [feilmelding, setFeilmelding] = useState(null)

	const [options, fetchOptions] = useAsyncFn(async tekst => {
		const { data } = await TpsfApi.soekPersoner(tekst)
		const options = []
		data.map(person => {
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

	const handleChange = tekst => {
		setSoekTekst(tekst)
		fetchOptions(tekst)
	}

	const navigerTilPerson = () => {
		naviger(ident).then(response => {
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
			<NavButton type="standard" onClick={openFinnModal}>
				Finn testperson
			</NavButton>

			<DollyModal
				isOpen={isFinnModalOpen}
				closeModal={closeFinnModal}
				width="40%"
				overflow="visible"
			>
				<h1>Søk etter testperson</h1>
				<p>
					Du kan søke på både FNR/DNR/BOST og navn, eller deler av disse. Du vil da få en liste av
					aktuelle personer. Velg en person for å bli tatt direkte til testdatagruppen personen
					finnes i.
				</p>
				<Label name="Testperson" label="Testperson">
					<AsyncSelect
						defaultOptions={false}
						loadOptions={fetchOptions}
						onInputChange={handleChange}
						inputValue={soekTekst}
						options={options}
						onChange={e => setIdent(e.value)}
						cacheOptions={true}
						label="Person"
						placeholder="Begynn å skrive inn FNR/DNR/BOST eller navn..."
					/>
				</Label>
				{feilmelding && <div className="error-message">{feilmelding}</div>}
				<NavButton type="standard" onClick={closeFinnModal} style={{ marginRight: '10px' }}>
					Avbryt
				</NavButton>
				<NavButton type="hoved" onClick={navigerTilPerson}>
					Gå til person
				</NavButton>
			</DollyModal>
		</>
	)
}
