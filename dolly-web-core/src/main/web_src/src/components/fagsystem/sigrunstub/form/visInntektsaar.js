import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const VisInntektsaar = ({ alleInntekter, setAlleInntekter }) => {
	return alleInntekter.map((inntekt, idx) => {
		return (
			<div key={idx}>
				<div>
					{/* Må formateres og oversettes med optionsmanager/kodeverk */}
					År: {inntekt.inntektsaar} {inntekt.inntektssted} {inntekt.tjeneste} {inntekt.tekniskNavn}{' '}
					{inntekt.verdi}
				</div>
				<div>
					<button onClick={() => _removeEnkeltinntekt(alleInntekter, setAlleInntekter, idx)}>
						-
					</button>
				</div>
			</div>
		)
	})
}

const _removeEnkeltinntekt = (alleInntekter, setAlleInntekter, idx) => {
	const copyAlleInntekter = [...alleInntekter]
	copyAlleInntekter.splice(idx, 1)

	return setAlleInntekter(copyAlleInntekter)
}
