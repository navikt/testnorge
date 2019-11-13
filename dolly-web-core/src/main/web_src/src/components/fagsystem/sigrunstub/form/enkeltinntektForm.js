import React, { useState } from 'react'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const EnkeltinntektForm = ({ enkeltinntekt, setEnkeltinntekt, setAlleInntekter }) => {
	return (
		<React.Fragment>
			<DollySelect
				name="tekniskNavn"
				label="Type inntekt"
				kodeverk="Summert skattegrunnlag" // Må formateres fra SUMMERT_SKATTEGRUNNLAG til Summert skattegrunnlag
				value={enkeltinntekt.tekniskNavn}
				onChange={e => setEnkeltinntekt({ ...enkeltinntekt, tekniskNavn: e && e.value })}
			/>
			<DollyTextInput
				name="verdi"
				label="Beløp"
				value={enkeltinntekt.verdi}
				onChange={e => setEnkeltinntekt({ ...enkeltinntekt, verdi: e.target.value })}
			/>
			<button
				onClick={() => _leggTilEnkeltinntekt(enkeltinntekt, setEnkeltinntekt, setAlleInntekter)}
			>
				Legg til
			</button>
		</React.Fragment>
	)
}

const _leggTilEnkeltinntekt = (enkeltinntekt, setEnkeltinntekt, setAlleInntekter) => {
	setAlleInntekter(prev => {
		prev.push(enkeltinntekt)
		return prev
	})
	setEnkeltinntekt({ ...enkeltinntekt, tekniskNavn: '', verdi: '' })
}
