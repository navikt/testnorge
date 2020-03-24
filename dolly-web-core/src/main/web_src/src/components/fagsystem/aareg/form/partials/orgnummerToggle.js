import React, { useState, useEffect } from 'react'
import _get from 'lodash/get'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AaregOrgnummerSelect } from './aaregOrgnummerSelect'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

const inputValg = { fraListe: 'velg', skrivSelv: 'skriv' }

export const OrgnummerToggle = ({ formikBag, path }) => {
	const [inputType, setInputType] = useState()

	useEffect(() => {
		const orgnr = _get(formikBag.values, path)
		const setInitial = async () => {
			const response = await SelectOptionsOppslag.hentOrgnr()
			if (!orgnr || response.liste.some(org => org.orgnr === orgnr)) {
				setInputType(inputValg.fraListe)
			} else setInputType(inputValg.skrivSelv)
		}
		setInitial()
	}, [])

	const handleToggleChange = event => {
		setInputType(event.target.value)
		formikBag.setFieldValue(path, '')
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.fraListe}
					value={inputValg.fraListe}
					checked={inputType === inputValg.fraListe}
				>
					Velg organisasjonsnummer
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.skrivSelv}
					value={inputValg.skrivSelv}
					checked={inputType === inputValg.skrivSelv}
				>
					Skriv inn organisasjonsnummer
				</ToggleKnapp>
			</ToggleGruppe>

			{inputType === inputValg.fraListe ? (
				<AaregOrgnummerSelect path={path} />
			) : (
				<FormikTextInput name={path} size="xlarge" />
			)}
		</div>
	)
}
