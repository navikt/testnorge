import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const VisAlleInntektsaar = ({ formikBag }) => {
	return (
		(formikBag.values.sigrunstub[0].grunnlag.length > 0 ||
			formikBag.values.sigrunstub[0].svalbardGrunnlag.length > 0) &&
		formikBag.values.sigrunstub.map((inntekt, idx) => {
			return (
				<div key={idx}>
					<div>
						{/* Må formateres og oversettes med optionsmanager/kodeverk */}
						{inntekt.inntektsaar} {inntekt.tjeneste}
						{inntekt.grunnlag.map((gr, jdx) => {
							return (
								<div key={jdx}>
									{gr.tekniskNavn} {gr.verdi}
								</div>
							)
						})}
						{inntekt.svalbardGrunnlag.map((sgr, kdx) => {
							return (
								<div key={kdx}>
									{sgr.tekniskNavn} {sgr.verdi}
								</div>
							)
						})}
					</div>
					<div>
						<button onClick={() => _removeEnkeltInntektsaar(formikBag, idx)}>-</button>
					</div>
				</div>
			)
		})
	)
}

const _removeEnkeltInntektsaar = (formikBag, idx) => {
	const copyAlleInntekter = [...alleInntekter]
	copyAlleInntekter.splice(idx, 1)

	//Fjerne inntektsår på en måte
}
